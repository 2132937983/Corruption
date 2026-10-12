package com.miophas.corruption.common.registry;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.CorruptionCurseApi;
import com.miophas.corruption.common.block.FlowingCorruptionBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.tags.FluidTags;
import org.junit.jupiter.api.Test;

/** Runs against the loaded mod: checks what the game actually registered, not just the definitions. */
class FlowingCorruptionRegistrationTest {
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("corruption", path); }

    @Test void fluidBlockAndBucketAreRegisteredAndPointAtEachOther() {
        var source = ModFluids.FLOWING_CORRUPTION.get();
        var flowing = ModFluids.FLOWING_CORRUPTION_FLOWING.get();
        assertEquals(id("flowing_corruption"), BuiltInRegistries.FLUID.getKey(source));
        assertEquals(id("flowing_corruption_flowing"), BuiltInRegistries.FLUID.getKey(flowing));
        assertSame(ModFluids.FLOWING_CORRUPTION_TYPE.get(), source.getFluidType());
        assertSame(source, flowing.getSource());
        assertTrue(source.isSame(flowing));
        assertTrue(source.defaultFluidState().isSource());
        assertFalse(source.defaultFluidState().is(FluidTags.WATER), "must not inherit water behaviour");

        var block = ModBlocks.FLOWING_CORRUPTION.get();
        assertInstanceOf(FlowingCorruptionBlock.class, block);
        assertEquals(id("flowing_corruption"), BuiltInRegistries.BLOCK.getKey(block));
        assertSame(source, block.defaultBlockState().getFluidState().getType());
        assertSame(block, source.defaultFluidState().createLegacyBlock().getBlock());
        // Fluid amount 7 of 8, not falling, is the block's level 1: the tallest flowing cell.
        var spreading = flowing.defaultFluidState().setValue(net.minecraft.world.level.material.FlowingFluid.LEVEL, 7).setValue(net.minecraft.world.level.material.FlowingFluid.FALLING, false);
        assertEquals(1, spreading.createLegacyBlock().getValue(LiquidBlock.LEVEL));
        assertSame(block, spreading.createLegacyBlock().getBlock());

        BucketItem bucket = ModItems.FLOWING_CORRUPTION_BUCKET.get();
        assertEquals(id("flowing_corruption_bucket"), BuiltInRegistries.ITEM.getKey(bucket));
        assertSame(source, bucket.content);
        assertSame(bucket, source.getBucket());
        assertSame(Items.BUCKET, bucket.getCraftingRemainingItem(new ItemStack(bucket)).getItem());
    }

    @Test void cursePotionsCarryTheDefinedCurse() {
        var definitions = CorruptionCurseApi.potions();
        assertEquals(definitions.size(), ModPotions.CORRUPTION_CURSE.size());
        for (int index = 0; index < definitions.size(); index++) {
            var definition = definitions.get(index);
            var potion = ModPotions.CORRUPTION_CURSE.get(index);
            assertEquals(id(definition.id()), BuiltInRegistries.POTION.getKey(potion.value()));
            assertEquals(1, potion.value().getEffects().size());
            MobEffectInstance effect = potion.value().getEffects().get(0);
            assertSame(ModEffects.CORRUPTION_CURSE.get(), effect.getEffect().value());
            assertEquals(definition.durationTicks(), effect.getDuration());
            assertEquals(definition.amplifier(), effect.getAmplifier());
            // All variants share one name, so one set of translation keys covers them.
            assertEquals("item.minecraft.potion.effect.corruption_curse", Items.POTION.getDescriptionId(PotionContents.createItemStack(Items.POTION, potion)));
        }
    }

    @Test void cursePotionsBrewLikeVanillaOnes() {
        PotionBrewing brewing = PotionBrewing.bootstrap(FeatureFlags.DEFAULT_FLAGS, net.minecraft.core.RegistryAccess.EMPTY);
        var base = ModPotions.CORRUPTION_CURSE.get(0);
        var extended = ModPotions.CORRUPTION_CURSE.get(1);
        var strong = ModPotions.CORRUPTION_CURSE.get(2);
        ItemStack bucket = new ItemStack(ModItems.FLOWING_CORRUPTION_BUCKET.get());
        ItemStack awkward = PotionContents.createItemStack(Items.POTION, Potions.AWKWARD);
        ItemStack brewed = brewing.mix(bucket, awkward);
        assertTrue(brewing.isIngredient(bucket));
        assertTrue(brewed.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).is(base));
        assertTrue(brewing.mix(new ItemStack(Items.REDSTONE), brewed).get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).is(extended));
        assertTrue(brewing.mix(new ItemStack(Items.GLOWSTONE_DUST), brewed).get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).is(strong));
        // Vanilla container recipes then turn any of them into splash and lingering potions.
        ItemStack splash = brewing.mix(new ItemStack(Items.GUNPOWDER), brewed);
        assertSame(Items.SPLASH_POTION, splash.getItem());
        assertTrue(splash.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).is(base));
        assertSame(Items.LINGERING_POTION, brewing.mix(new ItemStack(Items.DRAGON_BREATH), splash).getItem());
        // Water alone must not become a curse potion.
        assertFalse(brewing.hasMix(PotionContents.createItemStack(Items.POTION, Potions.WATER), bucket));
    }
}
