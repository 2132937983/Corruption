package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModCreativeModeTabs;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
 * The corruption's flashier magic on a real server: volleys of bolts fanned from the caster or dropped from the
 * sky, the corruption's own lightning; where each string is found; and what the creative tabs hold. The test world
 * never brings new entities into sight, so what a spell brings forth is caught as it joins the world.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptionMagicServerTest {
    private static FakePlayer caster(ServerLevel level, int x, String name) {
        TestWorld.load(level, x - 16, x + 16, 3000 - 16, 3000 + 16);
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x + .5, -60, 3000.5, 0, 0);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }
    private static List<Entity> catching(Runnable cast) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { cast.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        joined.forEach(Entity::discard);
        return joined;
    }
    private static double degrees(Vec3 way) { return Math.toDegrees(Math.atan2(way.x, way.z)); }

    @Test void aVolleyFansOutAcrossItsArcFromTheCaster(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_000, "volley_caster");
            ArcaneCasting.Origin origin = ArcaneCasting.Origin.of(player);
            SpellAction.LaunchBolt volley = new SpellAction.LaunchBolt("corruption:spell_bolt", 1.5, 0, List.of(), "", SpellAction.LaunchBolt.CORRUPTION_TRAIL, List.of(),
                    5, 40, SpellAction.Source.CASTER, SpellAction.LaunchBolt.SKY_HEIGHT);
            List<SpellProjectile> bolts = catching(() -> ArcaneCasting.world().perform(player, origin, volley)).stream()
                    .filter(SpellProjectile.class::isInstance).map(SpellProjectile.class::cast).toList();
            assertEquals(5, bolts.size(), "five bolts at once");
            double gaze = degrees(origin.direction());
            List<Double> turned = bolts.stream().map(bolt -> degrees(bolt.getDeltaMovement()) - gaze).sorted().toList();
            assertEquals(-20, turned.get(0), .5, "the first at one edge of the arc");
            assertEquals(20, turned.get(4), .5, "the last at the other");
            assertEquals(0, turned.get(2), .5, "the middle one along the gaze");
            for (SpellProjectile bolt : bolts) {
                assertEquals(origin.launch().y, bolt.getY(), 1e-6, "all set off from where the caster launches bolts");
                assertSame(player, bolt.getOwner());
            }
        });
    }

    @Test void boltsFromTheSkyFallOnTheOrigin(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_100, "sky_caster");
            Vec3 struck = new Vec3(48_106.5, -60, 3004.5);
            ArcaneCasting.Origin where = ArcaneCasting.Origin.impact(struck, new Vec3(1, 0, 0));
            SpellAction.LaunchBolt rain = new SpellAction.LaunchBolt("corruption:spell_bolt", 1.2, 0, List.of(), "", List.of(), List.of(),
                    8, 3, SpellAction.Source.SKY, 14);
            List<Entity> bolts = catching(() -> ArcaneCasting.world().perform(player, where, rain)).stream().filter(SpellProjectile.class::isInstance).toList();
            assertEquals(8, bolts.size());
            for (Entity bolt : bolts) {
                assertEquals(struck.y + 14, bolt.getY(), 1e-6, "fourteen blocks over where the bolt struck");
                assertTrue(Math.hypot(bolt.getX() - struck.x, bolt.getZ() - struck.z) <= 3 + 1e-6, "scattered no further than the spell says");
                assertTrue(bolt.getDeltaMovement().y < 0, "falling");
                assertEquals(0, bolt.getDeltaMovement().horizontalDistance(), 1e-6, "straight down");
            }
        });
    }

    @Test void corruptedLightningIsTheCorruptionsOwnBolt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_200, "tempest_caster");
            List<Entity> corrupted = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player),
                    new SpellAction.Lightning(6, 5, true, 4, "corruption:corruption")));
            assertEquals(5, corrupted.size(), "as many strikes as the spell says");
            for (Entity strike : corrupted) {
                assertEquals(ModEntities.CORRUPT_LIGHTNING.get(), strike.getType(), "the corruption's violet bolt, not the game's");
                assertTrue(strike.position().subtract(player.position()).horizontalDistance() <= 6 + 1, "within the spell's reach");
                assertEquals(-60, strike.getY(), 1e-6, "falling on the ground");
            }
            List<Entity> plain = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Lightning(6, 1)));
            assertEquals(List.of(EntityType.LIGHTNING_BOLT), plain.stream().map(Entity::getType).toList(), "lightning not said to be corrupt is the game's own");
        });
    }

    @Test void lightningFallsWhereTheBoltLandedEvenUnderARoof(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_300, "roof_caster");
            // A stone roof five blocks over the ground, as a cave's or a house's would be.
            for (int x = 48_310; x <= 48_316; x++) for (int z = 2997; z <= 3003; z++) level.setBlockAndUpdate(new BlockPos(x, -55, z), Blocks.STONE.defaultBlockState());
            Vec3 landed = new Vec3(48_313.5, -60, 3000.5);
            List<Entity> strikes = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.impact(landed, new Vec3(1, 0, 0)),
                    new SpellAction.Lightning(0, 2, true, 0, "")));
            assertEquals(2, strikes.size());
            for (Entity strike : strikes) assertEquals(-60, strike.getY(), 1e-6, "on the floor under the roof, not on top of it");
            assertEquals(new BlockPos(48_313, -60, 3000), SpellEffects.groundAt(level, new BlockPos(48_313, -57, 3000)), "a bolt struck in mid-air calls it down to the ground below");
            assertEquals(new BlockPos(48_313, -54, 3000), SpellEffects.groundAt(level, new BlockPos(48_313, -54, 3000)), "one that landed on the roof, onto the roof");
            BlockPos chasm = new BlockPos(48_313, 100, 3000);
            assertEquals(chasm, SpellEffects.groundAt(level, chasm), "with no ground in reach, where it struck");
        });
    }

    @Test void aSpellsEffectStandsOnTheGroundForItsTimeAndTellsTheClientsWhatItIs(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_400, "visual_caster");
            SpellAction.Visual circle = new SpellAction.Visual("corruption:magic_circle", 6, 2, 30, 0x8A0303, 0xFFB040, .5, "minecraft:entity.evoker.prepare_summon");
            List<SpellVisualEntity> shown = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), circle)).stream()
                    .filter(SpellVisualEntity.class::isInstance).map(SpellVisualEntity.class::cast).toList();
            assertEquals(1, shown.size());
            SpellVisualEntity effect = shown.get(0);
            assertEquals(-60, effect.getY(), 1e-6, "on the ground under the caster, not at their middle");
            assertEquals(player.getX(), effect.getX(), 1e-6);
            assertEquals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("corruption", "magic_circle"), effect.effect());
            assertEquals(30, effect.lifetime());
            assertEquals(0x8A0303, effect.colour());
            assertEquals(0xFFB040, effect.accent());
            assertEquals(.5F, effect.shake(), 1e-6);

            RegistryFriendlyByteBuf wire = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            effect.writeSpawnData(wire);
            SpellVisualEntity seen = ModEntities.SPELL_VISUAL.get().create(level);
            seen.readSpawnData(wire);
            assertEquals(List.of(effect.effect(), effect.radius(), effect.height(), effect.lifetime(), effect.colour(), effect.accent(), effect.shake()),
                    List.of(seen.effect(), seen.radius(), seen.height(), seen.lifetime(), seen.colour(), seen.accent(), seen.shake()), "a client sees it as it is");
            assertTrue(seen.getBoundingBoxForCulling().getXsize() >= 12, "and draws it whenever any of it could be in sight");

            assertFalse(seen.isPickable(), "nothing can hit it");
            seen.tickCount = 29;
            seen.tick();
            assertFalse(seen.isRemoved(), "it stands for its time");
            seen.tickCount = 30;
            seen.tick();
            assertTrue(seen.isRemoved(), "and is gone after it");
        });
    }

    @Test void aVolleyIsDrawnEvenlyAndShapesAreWhereTheySay() {
        Vec3 ahead = new Vec3(0, 0, 1);
        assertEquals(ahead, SpellEffects.fanned(ahead, 0, 1, 90), "one bolt flies along the gaze");
        assertEquals(ahead, SpellEffects.fanned(ahead, 2, 5, 0), "with no fan, all along it");
        assertEquals(-45, degrees(SpellEffects.fanned(ahead, 0, 3, 90)), 1e-4);
        assertEquals(45, degrees(SpellEffects.fanned(ahead, 2, 3, 90)), 1e-4);
        for (int point = 0; point < 12; point++) {
            Vec3 ring = SpellEffects.shapePoint(SpellAction.Shape.RING, 4, 2, point, 12);
            assertEquals(4, ring.horizontalDistance(), 1e-6, "a ring is as wide as its radius");
            assertEquals(0, ring.y, 1e-6, "and level with the origin");
            assertEquals(3, SpellEffects.shapePoint(SpellAction.Shape.SPHERE, 3, 2, point, 12).length(), 1e-6, "a sphere is round");
        }
        assertEquals(Vec3.ZERO, SpellEffects.shapePoint(SpellAction.Shape.COLUMN, 3, 5, 0, 6), "a column rises from the origin");
        assertEquals(new Vec3(0, 5, 0), SpellEffects.shapePoint(SpellAction.Shape.COLUMN, 3, 5, 5, 6), "as high as the spell says");
        assertEquals(6, SpellEffects.shapePoint(SpellAction.Shape.SPIRAL, 2, 6, 9, 10).y, 1e-6, "and so does a spiral");
    }

    @Test void eachStringIsFoundInItsBook(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var registries = level.registryAccess();
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:corruption_tempest"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_I.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:baal"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_IV.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:asmoday"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_VIII.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:andromalius"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_ASCENSION.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:pentacle_sun"))));
            assertEquals(List.of(ModItems.LEMEGETON.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:ars_goetia"))));
            assertEquals(List.of(), ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:no_such_string")));
            for (var key : ModItems.KEYS) assertFalse(ArcaneApi.strings(registries, new ItemStack(key.get())).isEmpty(), key.getId() + " reveals something");
        });
    }
    private static List<net.minecraft.world.item.Item> items(List<ItemStack> stacks) { return stacks.stream().map(ItemStack::getItem).toList(); }

    @Test void parchmentsHaveATabOfTheirOwnAndTheMainTabNoLongerTeachesWands(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var registries = level.registryAccess();
            List<ResourceKey<ArcaneString>> strings = ModCreativeModeTabs.inscribable(registries);
            int all = registries.registryOrThrow(ArcaneString.REGISTRY).size();
            assertEquals(all, strings.size(), "every string the data holds");
            assertEquals(all, new HashSet<>(strings).size(), "each once");
            assertEquals(ArcaneApi.key("corruption:corruption"), strings.get(0), "the Prologue's first");
            assertTrue(strings.indexOf(ArcaneApi.key("corruption:baal")) < strings.indexOf(ArcaneApi.key("corruption:andromalius")), "then the spirits in order");
            assertTrue(strings.indexOf(ArcaneApi.key("corruption:andromalius")) < strings.indexOf(ArcaneApi.key("corruption:pentacle_saturn")), "then the Ascension");
            assertEquals(ArcaneApi.key("corruption:ars_notoria"), strings.get(strings.size() - 1), "and the Lesser Key last");

            CreativeModeTab.ItemDisplayParameters shown = new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), false, registries);
            CreativeModeTab parchments = ModCreativeModeTabs.PARCHMENTS.get();
            parchments.buildContents(shown);
            assertEquals(all, parchments.getDisplayItems().size());
            assertTrue(parchments.getDisplayItems().stream().allMatch(stack -> stack.is(ModItems.PARCHMENT.get()) && ArcaneApi.inscription(stack) != null));
            CreativeModeTab main = ModCreativeModeTabs.CORRUPTION.get();
            main.buildContents(shown);
            for (var wand : ModItems.WANDS) assertTrue(main.contains(new ItemStack(wand.get())), wand.getId() + " is in the main tab");
            for (var key : ModItems.KEYS) assertTrue(main.contains(new ItemStack(key.get())), key.getId() + " is in the main tab");
            assertTrue(main.getDisplayItems().stream().noneMatch(stack -> ArcaneApi.inscription(stack) != null), "no inscribed parchments there");
            assertTrue(main.getDisplayItems().stream().noneMatch(stack -> !WandSpells.of(stack).isEmpty()), "nor wands taught a spell");
        });
    }
}
