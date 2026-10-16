package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.Grimoire;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Spells on a real server, where they are loaded from the mod's data: casting with the wand, creatures a spell
 * brings forth, and the pedestal wands are taught at. The test world never brings new creatures into sight, so
 * what a spell brings forth is caught as it joins the world.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class SpellServerTest {
    private static final String AGARES = "corruption:agares";

    /** A player of its own for each test, standing at x on the ground, with nothing on them. */
    private static FakePlayer caster(ServerLevel level, int x, String name) {
        TestWorld.load(level, x - 8, x + 8, 3000 - 8, 3000 + 8);
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x + .5, -60, 3000.5, 0, 0);
        player.getInventory().clearContent();
        player.setGameMode(GameType.SURVIVAL);
        player.setData(ModAttachments.PLAYER_CORRUPTION, 0);
        player.removeAllEffects();
        return player;
    }
    private static ItemStack parchment(String spell, int count) {
        return ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key(spell)).copyWithCount(count);
    }
    private static ItemStack wand(long energy, String... spells) {
        ItemStack wand = new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get());
        Grimoire grimoire = Grimoire.EMPTY;
        for (String spell : spells) grimoire = grimoire.inscribed(spell);
        WandSpells.set(wand, grimoire);
        wand.set(CorruptionEnergyCarrier.ENERGY.get(), energy);
        return wand;
    }
    private static long energy(ItemStack wand) { return ((ArcaneCastingItem) wand.getItem()).getEnergy(wand); }
    private static InteractionResult cast(ServerLevel level, FakePlayer player) {
        return player.getMainHandItem().getItem().use(level, player, InteractionHand.MAIN_HAND).getResult();
    }
    /** Runs this, catching whatever joins the world meanwhile. */
    private static List<Entity> catching(Runnable body) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { body.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        return joined;
    }

    @Test void aCastSpendsEnergyAParchmentAndCorruptionThenCools(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 46_000, "spell_caster");
            ItemStack wand = wand(20_000, AGARES);
            player.setItemInHand(InteractionHand.MAIN_HAND, wand);
            player.getInventory().setItem(5, parchment(AGARES, 2));
            try {
                assertEquals(InteractionResult.SUCCESS, cast(level, player));
                assertEquals(19_000, energy(wand), "agares costs 1 KCE");
                assertEquals(1, player.getInventory().getItem(5).getCount(), "and one parchment");
                assertEquals(1, PlayerCorruption.value(player), "and leaves its caster a little more corrupted");
                assertTrue(player.hasEffect(MobEffects.DAMAGE_RESISTANCE), "what it does: resistance for its caster");
                assertEquals(InteractionResult.FAIL, cast(level, player), "then it cools");
                player.getCooldowns().removeCooldown(wand.getItem());
                player.setData(ModAttachments.PLAYER_CORRUPTION, 19);
                assertEquals(InteractionResult.FAIL, cast(level, player), "one more would leave its caster wholly corrupted");
                assertEquals(19_000, energy(wand), "and a spell not cast costs nothing");
                assertEquals(19, PlayerCorruption.value(player));
                player.setData(ModAttachments.PLAYER_CORRUPTION, 0);
                player.getInventory().setItem(5, ItemStack.EMPTY);
                assertEquals(InteractionResult.FAIL, cast(level, player), "no parchment bearing it, no spell");
            } finally {
                player.getCooldowns().removeCooldown(wand.getItem());
                player.getInventory().clearContent();
                player.removeAllEffects();
            }
        });
    }

    @Test void inCreativePlayACastCostsNothing(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 46_016, "creative_caster");
            ItemStack wand = wand(0, AGARES);
            player.setItemInHand(InteractionHand.MAIN_HAND, wand);
            player.setGameMode(GameType.CREATIVE);
            try {
                assertEquals(InteractionResult.SUCCESS, cast(level, player), "without energy or parchment");
                assertEquals(0, energy(wand));
                assertEquals(0, PlayerCorruption.value(player));
                assertTrue(player.getCooldowns().isOnCooldown(wand.getItem()), "but it still cools");
            } finally {
                player.getCooldowns().removeCooldown(wand.getItem());
                player.setGameMode(GameType.SURVIVAL);
                player.getInventory().clearContent();
                player.removeAllEffects();
            }
        });
    }

    @Test void summonedCreaturesComeArmedAndNeverTurnOnTheirSummoner(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 46_032, "summoner");
            List<Entity> joined = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Summon("minecraft:skeleton", 2)));
            try {
                List<Skeleton> skeletons = joined.stream().filter(Skeleton.class::isInstance).map(Skeleton.class::cast).toList();
                assertEquals(2, skeletons.size(), joined.toString());
                for (Skeleton skeleton : skeletons) {
                    assertTrue(skeleton.getMainHandItem().is(Items.BOW), "brought forth as the game brings forth any skeleton, bow and all");
                    assertEquals(player.getUUID(), SpellSummons.summoner(skeleton));
                    assertTrue(skeleton.position().distanceTo(player.position()) < 4, "near the caster");
                }
                Skeleton first = skeletons.get(0);
                first.setTarget(player);
                assertNull(first.getTarget(), "never its summoner");
                first.setTarget(skeletons.get(1));
                assertNull(first.getTarget(), "nor another of their summons");
                Pig pig = EntityType.PIG.create(level);
                first.setTarget(pig);
                assertSame(pig, first.getTarget(), "anything else, as its nature is");
                assertTrue(catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Summon("minecraft:creeper", 1)))
                        .isEmpty(), "only what the spell_summons tag allows");
            } finally {
                joined.forEach(Entity::discard);
            }
        });
    }

    @Test void corruptionSpreadsFromTheMiddleOutward() {
        BlockPos middle = new BlockPos(10, 20, 30);
        List<BlockPos> places = SpellEffects.nearestFirst(middle, 2, net.minecraft.util.RandomSource.create(7));
        assertEquals(33, places.size(), "the blocks within two of the middle");
        assertEquals(middle, places.get(0));
        for (int at = 1; at < places.size(); at++) assertTrue(places.get(at - 1).distSqr(middle) <= places.get(at).distSqr(middle), "nearest first");
    }

    @Test void aPedestalIsMinedWithAPickaxeAndDropsItself(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockPos pos = new BlockPos(46_048, -50, 3002);
            TestWorld.load(level, pos.getX(), pos.getX(), pos.getZ(), pos.getZ());
            level.setBlock(pos, ModBlocks.CORRUPTION_ARCANE_PEDESTAL.get().defaultBlockState(), 3);
            BlockState state = level.getBlockState(pos);
            try {
                assertTrue(state.is(BlockTags.MINEABLE_WITH_PICKAXE), "mined with a pickaxe, as it needs the right tool");
                List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos));
                assertTrue(drops.size() == 1 && drops.get(0).is(ModItems.CORRUPTION_ARCANE_PEDESTAL.get()), "broken, it drops itself: " + drops);
            } finally {
                level.removeBlock(pos, false);
            }
        });
    }
}
