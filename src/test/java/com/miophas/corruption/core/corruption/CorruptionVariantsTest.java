package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.CorruptionApi;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CorruptionVariantsTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void identifiersKeepTheOriginalNamespaceAndDoNotDependOnRegistryNumbers() {
        assertEquals("corruption:generated/minecraft/stone", CorruptionApi.variantId(Blocks.STONE.defaultBlockState()).toString());
        assertEquals("corruption:generated/minecraft/oak_log", CorruptionApi.variantId(Blocks.OAK_LOG.defaultBlockState()).toString());
    }

    @Test
    void sourceSerializationRetainsPropertiesAndAxis() {
        var original = Blocks.OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
        assertSame(original, CorruptionApi.readSource(NbtUtils.writeBlockState(original)));
        var stair = Blocks.OAK_STAIRS.defaultBlockState();
        assertSame(stair, CorruptionApi.readSource(NbtUtils.writeBlockState(stair)));
    }

    @Test
    void missingModsAndInvalidOrUnsupportedStatesHaveASafeFallback() {
        for (String name : new String[] {"missing_mod:stone", "INVALID!", "corruption:infected_block"}) {
            CompoundTag tag = new CompoundTag();
            tag.putString("Name", name);
            assertSame(Blocks.STONE.defaultBlockState(), CorruptionApi.readSource(tag));
        }
        for (var block : new net.minecraft.world.level.block.Block[] {Blocks.AIR, Blocks.WATER}) {
            assertSame(Blocks.STONE.defaultBlockState(), CorruptionApi.readSource(NbtUtils.writeBlockState(block.defaultBlockState())));
        }
    }

    @Test
    void entityBlockAndWaterloggedSlabSourcesRetainTheirState() {
        for (var block : new net.minecraft.world.level.block.Block[] {Blocks.CHEST, Blocks.OAK_SIGN, Blocks.RED_BED, Blocks.SHULKER_BOX})
            for (var state : block.getStateDefinition().getPossibleStates())
                assertSame(state, CorruptionApi.readSource(NbtUtils.writeBlockState(state)));
        for (var state : Blocks.OAK_SLAB.getStateDefinition().getPossibleStates())
            assertSame(state, CorruptionApi.readSource(NbtUtils.writeBlockState(state)));
    }

    @Test
    void overlayUsesTheActualSlopedSurfaceNormalInsteadOfTheDeclaredCardinalFace() {
        float[] positions = {0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1};
        float[] normal = OverlayGeometry.faceNormal(positions, 0, 1, 0);
        assertEquals(0, normal[0], 0.00001F);
        assertEquals(1 / Math.sqrt(2), normal[1], 0.00001F);
        assertEquals(-1 / Math.sqrt(2), normal[2], 0.00001F);
        assertEquals(1, normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2], 0.00001F);
    }

    @Test
    void triangularAndDegenerateQuadsHaveUsableOffsetDirections() {
        assertArrayEquals(new float[] {0, 1, 0}, OverlayGeometry.faceNormal(
                new float[] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, 0, 1, 0), 0.00001F);
        assertArrayEquals(new float[] {1, 0, 0}, OverlayGeometry.faceNormal(new float[12], 1, 0, 0));
    }

    @Test
    void repeatedSurfacesHaveTheSameKeyDespiteVertexOrderingAndSignedZero() {
        float[] original = {0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1};
        float[] rotated = {1, 1, 1, -0.0F, 1, 1, -0.0F, 1, 0, 1, 1, 0};
        float[] reversed = {0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0};
        assertArrayEquals(OverlayGeometry.canonicalPositions(original), OverlayGeometry.canonicalPositions(rotated));
        assertArrayEquals(OverlayGeometry.canonicalPositions(original), OverlayGeometry.canonicalPositions(reversed));
    }

    @Test
    void nearbyDistinctSurfacesAreNotMerged() {
        float[] one = {0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1};
        float[] other = one.clone();
        for (int vertex = 0; vertex < 4; vertex++) other[vertex * 3 + 1] += 0.00001F;
        assertFalse(java.util.Arrays.equals(OverlayGeometry.canonicalPositions(one), OverlayGeometry.canonicalPositions(other)));
    }

    @Test
    void overlayUvKeepsCroppedAndRotatedTextureCoordinates() {
        assertEquals(0.65F, OverlayGeometry.remapUv(0.25F, 0.2F, 0.4F, 0.6F, 0.8F), 0.00001F);
        assertEquals(0.8F, OverlayGeometry.remapUv(0.2F, 0.2F, 0.4F, 0.8F, 0.6F), 0.00001F);
        assertEquals(0.6F, OverlayGeometry.remapUv(0.4F, 0.2F, 0.4F, 0.8F, 0.6F), 0.00001F);
        assertEquals(0.6F, OverlayGeometry.remapUv(0.2F, 0.2F, 0.2F, 0.6F, 0.8F));
    }
}
