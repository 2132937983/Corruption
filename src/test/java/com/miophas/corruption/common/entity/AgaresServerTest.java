package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Agares on a real server, in a flat world far from spawn. Each test ticks its own Agares by hand, so
 * nothing else moves and every tick is accounted for. It faces +Z when summoned, which puts its left at +X.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class AgaresServerTest {
    /** Puts it at x, z after loading the ground from so far behind and beside it to so far ahead. */
    private static Agares summon(ServerLevel level, int x, int z, int beside, int behind, int ahead) {
        TestWorld.load(level, x - beside, x + beside, z - behind, z + ahead);
        Agares agares = ModEntities.AGARES.get().create(level);
        // Facing is set because the game starts every mob off turned a few random degrees.
        agares.moveTo(x + .5, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z + .5, 0, 0);
        assertTrue(level.addFreshEntity(agares));
        return agares;
    }
    private static Pig pig(ServerLevel level, Agares agares, double left, double up, double forward) {
        Pig pig = EntityType.PIG.create(level);
        pig.setPos(agares.getX() + left, agares.getY() + up, agares.getZ() + forward);
        assertTrue(level.addFreshEntity(pig));
        return pig;
    }
    private static Colossus.Part part(Agares agares, String name) {
        for (var part : agares.getParts()) if (((Colossus.Part) part).definition().name().equals(name)) return (Colossus.Part) part;
        throw new AssertionError(name);
    }
    /** Ticks it until its current attack, and the screech it opens with, are over; answers every state it passed through. */
    private static List<AgaresAttack> fight(Agares agares, int ticks) {
        List<AgaresAttack> seen = new ArrayList<>();
        for (int tick = 0; tick < ticks; tick++) {
            agares.tick();
            if (seen.isEmpty() || seen.getLast() != agares.attack()) seen.add(agares.attack());
            if (seen.size() == 4) break;
        }
        return seen;
    }

    @Test void itStandsEightHundredBlocksTallInHitboxes(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 20000, 20000, 40, 40, 40);
            agares.tick();
            assertEquals(AgaresApi.maxHealth(), agares.getHealth());
            assertEquals(37, agares.getParts().length);
            assertFalse(agares.isPickable(), "only the parts can be hit");
            assertTrue(agares.bossBar().isPresent(), "clients are told which boss bar is its own");
            for (var part : agares.getParts()) assertTrue(level.getPartEntities().contains(part), "the world can find " + ((Colossus.Part) part).definition().name());
            double top = 0;
            for (var part : agares.getParts()) top = Math.max(top, part.getBoundingBox().maxY - agares.getY());
            assertTrue(top > 800, "its hitboxes reach " + top + " blocks up");
            assertEquals(agares.getZ() + 381.25, part(agares, "snout").getBoundingBox().getCenter().z, .01, "its snout is far out in front");
            assertEquals(agares.getZ() - 571.875, part(agares, "tail_6").getBoundingBox().getCenter().z, .01, "and its tail far behind");
            assertEquals(agares.getY(), part(agares, "left_foot").getBoundingBox().minY, .01);
            assertTrue(part(agares, "left_foot").getBoundingBox().getCenter().x > agares.getX() + 60 && part(agares, "right_wing_5").getBoundingBox().getCenter().x < agares.getX() - 500);
            assertTrue(agares.getEyeY() > agares.getY() + 780);
            agares.discard();
        });
    }

    @Test void itComesOnUntilItsPreyIsWellInsideItsReach(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 22000, 22000, 100, 100, 200);
            Pig bait = EntityType.PIG.create(level);
            bait.setPos(agares.getX(), agares.getY(), agares.getZ() + 500);
            agares.setTarget(bait);
            double startZ = agares.getZ(), ground = agares.getY();
            for (int tick = 0; tick < 100; tick++) agares.tick();
            assertEquals(startZ + 100 * AgaresApi.walkBlocksPerTick(), agares.getZ(), .01);
            assertEquals(ground, agares.getY(), 1e-6, "flat ground");
            assertEquals(AgaresAttack.NONE, agares.attack());
            assertEquals(1, agares.walk(1), 1e-6);
            assertEquals(ground, part(agares, "left_foot").getBoundingBox().minY, .01, "the left foot is planted");
            assertTrue(part(agares, "right_foot").getBoundingBox().minY > ground + 25, "while the right swings through the air");
            agares.discard();
        });
    }

    @Test void itScreechesThenRakesItsJawsThroughWhatIsInFront(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 24000, 24000, 130, 60, 620);
            Pig near = pig(level, agares, 0, 0, 300), far = pig(level, agares, 20, 0, 550), beside = pig(level, agares, 120, 0, 300);
            agares.setTarget(near);
            double startZ = agares.getZ();
            List<AgaresAttack> seen = new ArrayList<>();
            for (int tick = 0; tick < 400 && seen.size() < 4; tick++) {
                agares.tick();
                if (seen.isEmpty() || seen.getLast() != agares.attack()) seen.add(agares.attack());
                if (agares.attack() == AgaresAttack.SCREECH && agares.attackTicks(0) == AgaresAttack.SCREECH.strikeFrom() + 1) {
                    for (Pig cursed : List.of(near, beside)) assertTrue(cursed.hasEffect(ModEffects.CORRUPTION_CURSE), "the screech curses all that hear it");
                    assertFalse(far.hasEffect(ModEffects.CORRUPTION_CURSE), "but its jaws reach farther than its voice");
                }
                if (agares.attack() == AgaresAttack.BITE && agares.attackTicks(0) == AgaresAttack.BITE.strikeFrom()) assertTrue(near.isAlive() && far.isAlive(), "nothing is bitten before the jaws come down");
                if (agares.attack() == AgaresAttack.BITE && agares.attackTicks(0) == AgaresAttack.BITE.strikeFrom() + 2) assertTrue(!near.isAlive() && far.isAlive(), "the jaws reach the nearer first");
            }
            assertEquals(List.of(AgaresAttack.SCREECH, AgaresAttack.NONE, AgaresAttack.BITE, AgaresAttack.NONE), seen);
            assertFalse(near.isAlive()); assertFalse(far.isAlive(), "and are raked out to the farther");
            assertTrue(beside.isAlive() && beside.getHealth() == beside.getMaxHealth(), "what stands beside their path is untouched");
            assertEquals(startZ, agares.getZ(), 1e-6, "it stood its ground to strike");
            agares.discard(); beside.discard();
        });
    }

    @Test void itStompsOnWhatGetsUnderIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 26000, 26000, 120, 120, 120);
            Pig bait = pig(level, agares, -60, 0, 20);
            agares.setTarget(bait);
            double ground = agares.getY();
            List<AgaresAttack> seen = new ArrayList<>();
            float highest = 0;
            for (int tick = 0; tick < 400 && bait.isAlive(); tick++) {
                agares.tick();
                if (seen.isEmpty() || seen.getLast() != agares.attack()) seen.add(agares.attack());
                highest = Math.max(highest, (float) (part(agares, "right_foot").getBoundingBox().minY - ground));
            }
            assertEquals(List.of(AgaresAttack.SCREECH, AgaresAttack.NONE, AgaresAttack.STOMP_RIGHT), seen, "the foot on the side of its prey");
            assertTrue(highest > 70, "it raised that foot " + highest + " blocks");
            assertEquals(ground, part(agares, "right_foot").getBoundingBox().minY, .01, "and brought it down");
            assertFalse(bait.isAlive());
            agares.discard();
        });
    }

    @Test void itSweepsItsTailThroughWhatIsBehindIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 28000, 28000, 160, 420, 220);
            Pig behind = pig(level, agares, 40, 0, -300), behindToo = pig(level, agares, -150, 0, -300), ahead = pig(level, agares, 0, 0, 200);
            agares.setTarget(behind);
            assertEquals(List.of(AgaresAttack.SCREECH, AgaresAttack.NONE, AgaresAttack.TAIL_LEFT, AgaresAttack.NONE), fight(agares, 500));
            assertFalse(behind.isAlive(), "its prey is swept away");
            assertFalse(behindToo.isAlive(), "with everything else in the arc behind it");
            assertTrue(ahead.isAlive() && ahead.getHealth() == ahead.getMaxHealth(), "but nothing in front");
            agares.discard(); ahead.discard();
        });
    }

    @Test void itsWingsBeatDownWhatFliesInFrontOfIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 30000, 30000, 110, 260, 320);
            Pig flying = pig(level, agares, 30, 200, 250), grounded = pig(level, agares, -100, 0, 300), behind = pig(level, agares, 0, 0, -250);
            agares.setTarget(flying);
            assertEquals(List.of(AgaresAttack.SCREECH, AgaresAttack.NONE, AgaresAttack.GUST, AgaresAttack.NONE), fight(agares, 500), "only its wings reach prey in the air");
            assertFalse(flying.isAlive()); assertFalse(grounded.isAlive(), "the gust takes what is on the ground in front of it too");
            assertTrue(behind.isAlive() && behind.getHealth() == behind.getMaxHealth());
            agares.discard(); behind.discard();
        });
    }

    @Test void onlyAnAttackerCanHurtItAndItsHeadIsItsWeakPoint(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 32000, 32000, 40, 40, 40);
            Pig attacker = EntityType.PIG.create(level);
            attacker.setPos(agares.getX() + 600, agares.getY(), agares.getZ());
            agares.tick();
            assertFalse(agares.hurt(level.damageSources().inFire(), 1000));
            assertFalse(part(agares, "chest").hurt(level.damageSources().mobAttack(agares), 1000), "its own side cannot hurt it");
            assertEquals(agares.getMaxHealth(), agares.getHealth());
            assertTrue(part(agares, "left_wing_3").hurt(level.damageSources().mobAttack(attacker), 100));
            float body = agares.getMaxHealth() - agares.getHealth();
            agares.invulnerableTime = 0; agares.setHealth(agares.getMaxHealth());
            assertTrue(part(agares, "snout").hurt(level.damageSources().mobAttack(attacker), 100));
            float head = agares.getMaxHealth() - agares.getHealth();
            assertTrue(body > 0 && body < 10.01F, "a tenth gets through, less its armour: " + body);
            assertTrue(head > body * 1.9F, "the head takes more: " + head + " against " + body);
            agares.discard();
        });
    }

    @Test void itDiesSlowly(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Agares agares = summon(level, 34000, 34000, 40, 40, 40);
            agares.tick();
            double standing = part(agares, "hips").getBoundingBox().minY;
            assertTrue(agares.hurt(level.damageSources().genericKill(), Float.MAX_VALUE), "commands still work on it");
            for (int tick = 0; tick < AgaresApi.deathTicks() - 1; tick++) agares.tick();
            assertFalse(agares.isRemoved(), "still sinking");
            assertTrue(part(agares, "hips").getBoundingBox().minY < standing - 100, "its hitboxes sink with it");
            agares.tick();
            assertTrue(agares.isRemoved());
        });
    }
}
