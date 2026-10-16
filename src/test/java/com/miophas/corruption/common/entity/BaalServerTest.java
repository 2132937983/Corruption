package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Baal on a real server, in a flat world far from spawn. Each test ticks its own Baal by hand, so nothing
 * else moves and every tick is accounted for. It faces +Z when summoned, which puts its left at +X.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class BaalServerTest {
    private static void onServer(MinecraftServer server, Consumer<ServerLevel> body) { TestWorld.onServer(server, body); }

    /** Loads the ground this many chunks around, for it to walk on and for others to stand on, then puts it there. */
    private static Baal summon(ServerLevel level, int x, int z, int chunks) {
        for (int chunkX = (x >> 4) - chunks; chunkX <= (x >> 4) + chunks; chunkX++)
            for (int chunkZ = (z >> 4) - chunks; chunkZ <= (z >> 4) + chunks; chunkZ++) level.getChunk(chunkX, chunkZ);
        Baal baal = ModEntities.BAAL.get().create(level);
        // Facing is set because the game starts every mob off turned a few random degrees.
        baal.moveTo(x + .5, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z + .5, 0, 0);
        assertTrue(level.addFreshEntity(baal));
        return baal;
    }
    private static Pig pig(ServerLevel level, Baal baal, double left, double forward) {
        Pig pig = EntityType.PIG.create(level);
        pig.setPos(baal.getX() + left, baal.getY(), baal.getZ() + forward);
        assertTrue(level.addFreshEntity(pig));
        return pig;
    }
    private static Colossus.Part part(Baal baal, String name) {
        for (var part : baal.getParts()) if (((Colossus.Part) part).definition().name().equals(name)) return (Colossus.Part) part;
        throw new AssertionError(name);
    }

    @Test void itStandsAsAColossusOfHitboxes(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 4000, 4000, 2);
            baal.tick();
            assertEquals(BaalApi.maxHealth(), baal.getHealth());
            assertEquals(23, baal.getParts().length);
            assertFalse(baal.isPickable(), "only the parts can be hit");
            for (var part : baal.getParts()) {
                assertEquals(baal.getId() + 1 + List.of(baal.getParts()).indexOf(part), part.getId());
                assertTrue(level.getPartEntities().contains(part), "the world can find " + ((Colossus.Part) part).definition().name());
            }
            var head = part(baal, "head").getBoundingBox();
            assertEquals(baal.getY() + 147.5, head.minY, .5); assertEquals(38.75, head.getXsize(), .01);
            assertEquals(baal.getZ() + 51.25, head.getCenter().z, .01, "the head is out in front");
            assertEquals(baal.getY() + 233.75, part(baal, "left_wing").getBoundingBox().maxY, .5, "the highest hitbox ends at the claw tips");
            var left = part(baal, "left_hoof").getBoundingBox();
            var right = part(baal, "right_hoof").getBoundingBox();
            assertEquals(baal.getY(), left.minY, .01); assertEquals(baal.getY(), right.minY, .01);
            assertTrue(left.getCenter().x > baal.getX() + 20 && right.getCenter().x < baal.getX() - 20, "hooves either side of it");
            assertTrue(baal.getEyeY() > baal.getY() + 160);
            baal.discard();
        });
    }

    @Test void itWalksTowardsWhatItHuntsAndItsLegsKeepStep(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 6000, 6000, 6);
            Pig bait = EntityType.PIG.create(level);
            bait.setPos(baal.getX(), baal.getY(), baal.getZ() + 400);
            baal.setTarget(bait);
            double startZ = baal.getZ(), ground = baal.getY();
            float highest = 0;
            for (int tick = 0; tick < 200; tick++) {
                baal.tick();
                highest = Math.max(highest, (float) (part(baal, "right_hoof").getBoundingBox().minY - ground));
            }
            assertEquals(startZ + 200 * BaalApi.walkBlocksPerTick(), baal.getZ(), .01);
            assertEquals(6000.5, baal.getX(), .05, "straight ahead");
            assertEquals(ground, baal.getY(), 1e-6, "flat ground");
            assertEquals(BaalApi.cycles(200 * BaalApi.walkBlocksPerTick()), baal.cycle(1), 1e-3);
            assertEquals(1, baal.walk(1), 1e-6);
            assertEquals(BaalAttack.NONE, baal.attack(), "too far away to strike or to roar at");
            assertTrue(highest > 15, "the right hoof swung through the air: " + highest);
            // Six tenths of a cycle in, the right hoof has come down ahead and the left has just lifted far behind it.
            assertEquals(ground, part(baal, "right_hoof").getBoundingBox().minY, .01);
            assertTrue(part(baal, "right_hoof").getBoundingBox().getCenter().z > part(baal, "left_hoof").getBoundingBox().getCenter().z + 30);
            assertEquals(baal.getZ() + 51.25, part(baal, "head").getBoundingBox().getCenter().z, .01, "the body's hitboxes came with it");
            baal.discard();
        });
    }

    @Test void itRoarsThenSlamsWhatStandsInFrontOfIt(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 8000, 8000, 10);
            Pig bait = pig(level, baal, 0, 100), bystander = pig(level, baal, 120, 0), distant = pig(level, baal, 0, -150);
            baal.setTarget(bait);
            List<BaalAttack> seen = new ArrayList<>();
            double startZ = baal.getZ();
            for (int tick = 0; tick < 260 && bait.isAlive(); tick++) {
                baal.tick();
                if (seen.isEmpty() || seen.getLast() != baal.attack()) seen.add(baal.attack());
                if (baal.attack() == BaalAttack.ROAR && baal.attackTicks(0) == BaalAttack.ROAR.impactTick() + 1) {
                    for (Pig cursed : List.of(bait, bystander, distant)) assertTrue(cursed.hasEffect(ModEffects.CORRUPTION_CURSE), "the roar curses all that hear it");
                }
            }
            assertEquals(List.of(BaalAttack.ROAR, BaalAttack.NONE, BaalAttack.SLAM_LEFT), seen);
            assertEquals(BaalAttack.SLAM_LEFT.impactTick() + 1, baal.attackTicks(0), 1e-6, "the blow landed on its beat");
            assertFalse(bait.isAlive(), "the slam kills what it lands on");
            assertTrue(bystander.isAlive() && bystander.getHealth() == bystander.getMaxHealth(), "and nothing well outside it");
            assertEquals(startZ, baal.getZ(), 1e-6, "it stood its ground to strike");
            baal.discard(); bystander.discard(); distant.discard();
        });
    }

    @Test void itStompsOnWhatGetsUnderIt(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 10000, 10000, 2);
            Pig bait = pig(level, baal, -20, 5);
            baal.setTarget(bait);
            List<BaalAttack> seen = new ArrayList<>();
            double ground = baal.getY();
            float highest = 0;
            for (int tick = 0; tick < 260 && bait.isAlive(); tick++) {
                baal.tick();
                if (seen.isEmpty() || seen.getLast() != baal.attack()) seen.add(baal.attack());
                highest = Math.max(highest, (float) (part(baal, "right_hoof").getBoundingBox().minY - ground));
            }
            assertEquals(List.of(BaalAttack.ROAR, BaalAttack.NONE, BaalAttack.STOMP_RIGHT), seen, "the hoof on the side of its prey");
            assertTrue(highest > 25, "it raised that hoof: " + highest);
            assertEquals(ground, part(baal, "right_hoof").getBoundingBox().minY, .01, "and brought it down");
            assertFalse(bait.isAlive());
            baal.discard();
        });
    }

    @Test void onlyAnAttackerCanHurtItAndTheHeadIsItsWeakPoint(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 12000, 12000, 2);
            Pig attacker = EntityType.PIG.create(level);
            attacker.setPos(baal.getX() + 300, baal.getY(), baal.getZ());
            baal.tick();
            assertFalse(baal.hurt(level.damageSources().inFire(), 1000));
            assertFalse(baal.hurt(level.damageSources().drown(), 1000));
            assertFalse(part(baal, "chest").hurt(level.damageSources().mobAttack(baal), 1000), "its own side cannot hurt it");
            assertEquals(baal.getMaxHealth(), baal.getHealth());

            assertTrue(part(baal, "chest").hurt(level.damageSources().mobAttack(attacker), 100));
            float body = baal.getMaxHealth() - baal.getHealth();
            assertSame(attacker, baal.getTarget(), "it turns on what hurt it");
            baal.invulnerableTime = 0; baal.setHealth(baal.getMaxHealth());
            assertTrue(part(baal, "head").hurt(level.damageSources().mobAttack(attacker), 100));
            float head = baal.getMaxHealth() - baal.getHealth();
            assertTrue(body > 0 && body < 25.01F, "a quarter gets through, less its armour: " + body);
            assertTrue(head > body * 2, "the head takes more: " + head + " against " + body);
            baal.discard();
        });
    }

    /**
     * With no player about, it goes after the nearest living thing it sees that is not of the corruption, as the
     * corruption's remnants do; never one of the corruption's own, however near.
     */
    @Test void withNoPlayerAboutItHuntsWhatIsNotOfTheCorruption(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 18_000, 18_000, 3);
            Pig pig = pig(level, baal, 0, 24);
            pig.setNoAi(true);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            remnant.setPos(baal.getX(), baal.getY(), baal.getZ() + 14);
            remnant.setNoAi(true);
            assertTrue(level.addFreshEntity(remnant));
            try {
                assertTrue(baal.isAlliedTo(remnant), "the corruption's own are its allies");
                baal.setTarget(null);
                baal.tickCount = 20;
                baal.customServerAiStep();
                assertSame(pig, baal.getTarget(), "the beast it sees, with no player about; not the remnant nearer to it");
            } finally {
                pig.discard();
                remnant.discard();
                baal.discard();
            }
        });
    }

    @Test void itDiesSlowly(MinecraftServer server) {
        onServer(server, level -> {
            Baal baal = summon(level, 14000, 14000, 2);
            baal.tick();
            assertTrue(baal.hurt(level.damageSources().genericKill(), Float.MAX_VALUE), "commands still work on it");
            assertTrue(baal.isDeadOrDying());
            double heart = baal.heart(1).y - baal.getY();
            assertFalse(part(baal, "head").isPickable(), "nothing to strike at once it is dying");
            for (int tick = 0; tick < BaalApi.deathTicks() - 1; tick++) baal.tick();
            assertFalse(baal.isRemoved(), "still sinking");
            assertTrue(baal.collapse(0) > .98F);
            double sunk = part(baal, "head").getBoundingBox().minY - baal.getY();
            assertTrue(sunk < 147.5 - 20, "its hitboxes sink with it: " + sunk);
            assertTrue(baal.heart(1).y - baal.getY() < heart - BaalApi.heightBlocks() * .15, "and its heart, where its light pours out, sinks further, with its body");
            baal.tick();
            assertFalse(baal.isRemoved(), "its body is gone in its last burst, but it lingers while its death plays out");
            for (int tick = 0; tick < Baal.DEATH.linger() - 1; tick++) baal.tick();
            assertFalse(baal.isRemoved());
            baal.tick();
            assertTrue(baal.isRemoved(), "and then it is gone");
        });
    }

    @Test void theWorldCanSummonIt(MinecraftServer server) {
        onServer(server, level -> {
            level.getChunk(16000 >> 4, 16000 >> 4);
            // What /summon corruption:baal does.
            CompoundTag tag = new CompoundTag();
            tag.putString("id", "corruption:baal");
            var summoned = EntityType.loadEntityRecursive(tag, level, entity -> { entity.moveTo(new Vec3(16000.5, -60, 16000.5)); return entity; });
            assertInstanceOf(Baal.class, summoned);
            assertTrue(level.tryAddFreshEntityWithPassengers(summoned));
            assertTrue(((LivingEntity) summoned).isAlive());
            summoned.discard();
        });
    }
}
