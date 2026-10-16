package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonParser;
import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.arcane.SpellCodecs;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellRule;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * What a spell's damage is worth where it lands, measured: Baal's fireball, cast from its own data and flown at a
 * creature, takes from it the 240 it says, unless what the creature wears takes its share first (spells hurt as most
 * things do: armour, enchantments that protect, Resistance, and whatever other mods put on a creature all count);
 * and its blast tears through obsidian, but not bedrock.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class SpellDamageServerTest {
    private static FakePlayer caster(ServerLevel level, int x, String name) {
        TestWorld.load(level, x - 16, x + 32, 3000 - 16, 3000 + 32);
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x + .5, -60, 3000.5, 0, 0);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }
    private static SpellRule spell(String name) throws Exception {
        try (InputStream in = SpellDamageServerTest.class.getResourceAsStream("/data/corruption/corruption/arcane_spell/" + name + ".json")) {
            assertNotNull(in);
            return SpellCodecs.RULE.parse(JsonOps.INSTANCE, JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))).getOrThrow();
        }
    }
    /** A creature that will stand still and has health enough to show all a blow takes from it. */
    private static <T extends Mob> T target(ServerLevel level, EntityType<T> type, Vec3 at) {
        T mob = type.create(level);
        mob.moveTo(at.x, at.y, at.z, 0, 0);
        mob.setNoAi(true);
        mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);
        mob.setHealth(1000);
        level.addFreshEntity(mob);
        return mob;
    }
    /** Flies this spell's bolt from the caster's eyes straight at this point, a tick at a time, until it is spent; what joined the world meanwhile is let go. */
    private static void fire(ServerLevel level, FakePlayer caster, SpellRule spell, Vec3 at) {
        SpellAction.LaunchBolt bolt = spell.actions().stream().filter(SpellAction.LaunchBolt.class::isInstance).map(SpellAction.LaunchBolt.class::cast).findFirst().orElseThrow();
        Vec3 from = new Vec3(caster.getX(), caster.getEyeY() - .1, caster.getZ());
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try {
            ArcaneCasting.world().perform(caster, new ArcaneCasting.Origin(caster.getBoundingBox().getCenter(), from, at.subtract(from).normalize()), bolt);
            SpellProjectile flying = joined.stream().filter(SpellProjectile.class::isInstance).map(SpellProjectile.class::cast).findFirst().orElseThrow();
            for (int tick = 0; tick < 60 && !flying.isRemoved(); tick++) flying.tick();
            assertTrue(flying.isRemoved(), "the bolt struck");
        } finally {
            NeoForge.EVENT_BUS.unregister(catcher);
            joined.stream().filter(entity -> !(entity instanceof LivingEntity)).forEach(Entity::discard);
        }
    }

    @Test void baalsFireballTakesTheTwoHundredAndFortyItSaysAndArmourTakesItsShare(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 74_000, "baal_damage");
            SpellRule baal = rethrow(() -> spell("baal"));
            var pig = target(level, EntityType.PIG, new Vec3(player.getX(), -60, player.getZ() + 8));
            try {
                fire(level, player, baal, pig.getBoundingBox().getCenter());
                assertEquals(240, 1000 - pig.getHealth(), 1e-3, "all of it, on what wears nothing");
            } finally {
                pig.discard();
            }
            var knight = target(level, EntityType.ZOMBIE, new Vec3(player.getX() + 12, -60, player.getZ() + 8));
            knight.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            knight.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            knight.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            knight.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            FakePlayer second = caster(level, 74_012, "baal_damage_armour");
            try {
                float armour = (float) knight.getAttributeValue(Attributes.ARMOR), toughness = (float) knight.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                fire(level, second, baal, knight.getBoundingBox().getCenter());
                float taken = 1000 - knight.getHealth();
                assertTrue(taken < 240, "armour takes its share: " + taken);
                assertEquals(CombatRules.getDamageAfterAbsorb(knight, 240, level.damageSources().generic(), armour, toughness), taken, .5F,
                        "just as much as the game's armour takes from any blow (armour " + armour + ", toughness " + toughness + ")");
            } finally {
                knight.discard();
            }
        });
    }

    @Test void baalsFireballTearsThroughObsidianButNotBedrock(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 74_400, "baal_obsidian");
            int x = (int) Math.floor(player.getX()), z = (int) Math.floor(player.getZ()) + 8;
            List<BlockPos> wall = new ArrayList<>();
            for (int across = -2; across <= 2; across++) for (int up = 0; up < 4; up++) wall.add(new BlockPos(x + across, -60 + up, z));
            BlockPos bedrock = new BlockPos(x, -59, z + 1);
            try {
                wall.forEach(at -> level.setBlock(at, Blocks.OBSIDIAN.defaultBlockState(), 3));
                level.setBlock(bedrock, Blocks.BEDROCK.defaultBlockState(), 3);
                fire(level, player, rethrow(() -> spell("baal")), Vec3.atCenterOf(new BlockPos(x, -59, z)));
                long torn = wall.stream().filter(at -> level.getBlockState(at).isAir()).count();
                assertTrue(torn >= 10, "the obsidian it struck torn through: " + torn + " of " + wall.size());
                assertTrue(level.getBlockState(bedrock).is(Blocks.BEDROCK), "but never bedrock");
            } finally {
                wall.forEach(at -> level.setBlock(at, Blocks.AIR.defaultBlockState(), 3));
                level.setBlock(bedrock, Blocks.AIR.defaultBlockState(), 3);
            }
        });
    }

    private interface Reading<T> { T read() throws Exception; }
    private static <T> T rethrow(Reading<T> reading) {
        try { return reading.read(); }
        catch (Exception failure) { throw new IllegalStateException(failure); }
    }
}
