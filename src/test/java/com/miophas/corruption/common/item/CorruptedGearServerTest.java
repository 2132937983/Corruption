package com.miophas.corruption.common.item;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneWeapons;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.common.alchemy.AlchemyInput;
import com.miophas.corruption.common.alchemy.ShapedAlchemyRecipe;
import com.miophas.corruption.common.altar.AltarInput;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.entity.Andromalius;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.ArcaneBladeRules;
import com.miophas.corruption.core.corruption.CorruptedRobeRules;
import com.miophas.corruption.core.corruption.CorruptedRobeRules.Tier;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The corrupted robes, the corruption's blades and what is made toward them, on a real server: the hold a set of robes
 * keeps on a player's corruption, and its giving it back; the veil they lay over their wearer; the corruption's hurt
 * they turn aside; the blades' weight, their mark and their curse; the recipes that keep what was set on what they
 * remake; the corruption's working blocks made at a crafting table; its plants given up whole to shears and Silk Touch;
 * and its living plants' own matter, left to a player's kill.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptedGearServerTest {
    private static FakePlayer player(ServerLevel level, String name, int corruption) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        for (EquipmentSlot slot : EquipmentSlot.values()) player.setItemSlot(slot, ItemStack.EMPTY);
        player.setData(ModAttachments.PLAYER_CORRUPTION, corruption);
        player.setData(ModAttachments.CORRUPTION_KEPT, CorruptedRobeRules.NOTHING_KEPT);
        player.setData(ModAttachments.CORRUPTION_HELD, CorruptedRobeRules.NOTHING_KEPT);
        return player;
    }
    private static Villager villager(ServerLevel level, double x, double z) {
        Villager villager = EntityType.VILLAGER.create(level);
        villager.moveTo(x, -60, z, 0, 0);
        villager.setNoAi(true);
        level.addFreshEntity(villager);
        return villager;
    }
    private static IronGolem golem(ServerLevel level, double x, double z) {
        IronGolem golem = EntityType.IRON_GOLEM.create(level);
        golem.moveTo(x, -60, z, 0, 0);
        golem.setNoAi(true);
        level.addFreshEntity(golem);
        return golem;
    }
    /** Puts on a whole set of one grade, each piece where it is worn; or takes every piece off, for none. */
    private static void dress(LivingEntity wearer, @Nullable Tier grade) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET})
            wearer.setItemSlot(slot, ItemStack.EMPTY);
        if (grade == null) return;
        for (DeferredItem<CorruptedRobeArmorItem> robe : ModItems.CORRUPTED_ROBES)
            if (robe.get().robeTier() == grade) wearer.setItemSlot(robe.get().getEquipmentSlot(), new ItemStack(robe.get()));
    }
    private static ItemStack piece(Tier grade, ArmorItem.Type type) {
        return ModItems.CORRUPTED_ROBES.stream().map(DeferredItem::get).filter(robe -> robe.robeTier() == grade && robe.getType() == type)
                .map(ItemStack::new).findFirst().orElseThrow();
    }
    private static Holder<Enchantment> enchantment(ServerLevel level, ResourceKey<Enchantment> key) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
    }
    /** What a player strikes for with this in hand: their own 1, and what it adds. */
    private static double strikes(ItemStack weapon) {
        double total = 1;
        for (var entry : weapon.getAttributeModifiers().modifiers())
            if (entry.attribute().value() == Attributes.ATTACK_DAMAGE.value() && entry.slot().test(EquipmentSlot.MAINHAND)) total += entry.modifier().amount();
        return total;
    }
    private static Recipe<?> recipe(ServerLevel level, String path) {
        return level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", path))
                .orElseThrow(() -> new AssertionError("corruption:" + path + " loads")).value();
    }
    /** What a block gives up to this tool, each kind once. */
    private static List<Item> drops(ServerLevel level, String block, ItemStack tool) {
        Block broken = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("corruption", block));
        return Block.getDrops(broken.defaultBlockState(), level, BlockPos.ZERO, null, null, tool).stream().map(ItemStack::getItem).distinct().toList();
    }
    /** What a creature leaves, killed by this player or by nothing in particular. */
    private static List<Item> killed(ServerLevel level, LivingEntity creature, @Nullable Player by) {
        LootParams.Builder params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, creature)
                .withParameter(LootContextParams.ORIGIN, creature.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, by == null ? level.damageSources().generic() : level.damageSources().playerAttack(by));
        if (by != null) params.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, by).withParameter(LootContextParams.ATTACKING_ENTITY, by);
        return level.getServer().reloadableRegistries().getLootTable(creature.getLootTable()).getRandomItems(params.create(LootContextParamSets.ENTITY))
                .stream().map(ItemStack::getItem).toList();
    }

    @Test void aSetOfCorruptedRobesRaisesALowCorruptionToItsThresholdAndHoldsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer wearer = player(level, "robe_raised", 3);
            dress(wearer, Tier.NOVICE);
            CorruptedRobes.hold(wearer);
            assertEquals(5, PlayerCorruption.value(wearer), "below the novice's five: raised to it");
            assertEquals(3, wearer.getData(ModAttachments.CORRUPTION_KEPT), "what it was, kept");
            PlayerCorruption.raise(wearer, 3);
            assertEquals(5, PlayerCorruption.value(wearer), "nothing raises it while it is held");
            new ItemStack(ModItems.PURIFIED_WATER.get()).finishUsingItem(level, wearer);
            assertEquals(5, PlayerCorruption.value(wearer), "nor lowers it");
            dress(wearer, null);
            CorruptedRobes.hold(wearer);
            assertEquals(3, PlayerCorruption.value(wearer), "taken off: what it was, given back");
        });
    }

    @Test void aSetOfCorruptedRobesNeverLowersAHighCorruptionAndHoldsItWhereItIs(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer wearer = player(level, "robe_holder", 8);
            dress(wearer, Tier.NOVICE);
            CorruptedRobes.hold(wearer);
            assertEquals(8, PlayerCorruption.value(wearer), "above the novice's five: never lowered");
            PlayerCorruption.raise(wearer, 3);
            new ItemStack(ModItems.PURIFIED_WATER.get()).finishUsingItem(level, wearer);
            assertEquals(8, PlayerCorruption.value(wearer), "neither raised nor lowered while it is held");
            wearer.setData(ModAttachments.PLAYER_CORRUPTION, 2);
            CorruptedRobes.hold(wearer);
            assertEquals(8, PlayerCorruption.value(wearer), "and whatever else moves it is put back");
            dress(wearer, Tier.INTERMEDIATE);
            CorruptedRobes.hold(wearer);
            assertEquals(12, PlayerCorruption.value(wearer), "the intermediate's twelve raises it");
            dress(wearer, Tier.ADVANCED);
            CorruptedRobes.hold(wearer);
            assertEquals(19, PlayerCorruption.value(wearer), "the advanced's nineteen");
            wearer.setItemSlot(EquipmentSlot.HEAD, piece(Tier.NOVICE, ArmorItem.Type.HELMET));
            CorruptedRobes.hold(wearer);
            assertEquals(19, PlayerCorruption.value(wearer), "a lower grade (mixed grades, the lowest of them) never lowers it");
            wearer.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            CorruptedRobes.hold(wearer);
            assertEquals(8, PlayerCorruption.value(wearer), "three pieces are no set: what it was, given back");
            assertEquals(CorruptedRobeRules.NOTHING_KEPT, wearer.getData(ModAttachments.CORRUPTION_KEPT), "and nothing kept any more");
            assertEquals(CorruptedRobeRules.NOTHING_KEPT, wearer.getData(ModAttachments.CORRUPTION_HELD), "nor held");
            PlayerCorruption.raise(wearer, 1);
            assertEquals(9, PlayerCorruption.value(wearer), "free to grow again");
        });
    }

    /**
     * Death takes all of a player's corruption, robe or no robe, and what a robe kept of it with it: so a robe lost in
     * dying gives nothing back. Played through the game's own respawn event, as a real death is.
     */
    @Test void deathTakesTheCorruptionAndWhatTheRobesKeptOfIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer dying = player(level, "robe_dier", 8);
            dress(dying, Tier.ADVANCED);
            CorruptedRobes.hold(dying);
            assertEquals(19, PlayerCorruption.value(dying), "held at the advanced's nineteen");
            FakePlayer respawned = player(level, "robe_respawned", 0);
            NeoForge.EVENT_BUS.post(new PlayerEvent.Clone(respawned, dying, true));
            assertEquals(0, PlayerCorruption.value(respawned), "death takes the corruption");
            assertEquals(CorruptedRobeRules.NOTHING_KEPT, respawned.getData(ModAttachments.CORRUPTION_KEPT), "and what the robe kept of it");
            CorruptedRobes.hold(respawned);
            assertEquals(0, PlayerCorruption.value(respawned), "so the robe, lost in dying, gives nothing back");
        });
    }

    @Test void theRobesVeilTheirWearerFromTheCorruptionsCreaturesAndOnlyTheHighestFromItsLords(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            TestWorld.load(level, 130_000 - 40, 130_000 + 40, 3000 - 40, 3000 + 40);
            Villager wearer = villager(level, 130_000.5, 3000.5);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            Andromalius lord = ModEntities.ANDROMALIUS.get().create(level);
            try {
                assertTrue(remnant.canAttack(wearer), "unveiled, the corruption's creatures may take it for prey");
                assertTrue(lord.canAttack(wearer), "and its lords");
                for (Tier grade : List.of(Tier.NOVICE, Tier.INTERMEDIATE)) {
                    dress(wearer, grade);
                    assertTrue(CorruptionVeilApi.hiddenFrom(wearer, remnant), grade + ": hidden from its creatures");
                    assertFalse(remnant.canAttack(wearer), grade + ": which do not take it for prey");
                    assertFalse(CorruptionVeilApi.hiddenFrom(wearer, lord), grade + ": but not from its lords");
                    assertTrue(lord.canAttack(wearer), grade + ": who still do");
                }
                dress(wearer, Tier.ADVANCED);
                assertFalse(remnant.canAttack(wearer));
                assertFalse(lord.canAttack(wearer), "the highest grade hides it from its lords too");
                assertFalse(CorruptionVeilApi.hiddenFrom(wearer, EntityType.ZOMBIE.create(level)), "what is not of the corruption sees as it always did");
                wearer.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
                assertTrue(remnant.canAttack(wearer), "three pieces veil nothing");
            } finally {
                wearer.discard();
            }
        });
    }

    @Test void theRobesTurnAsideTheCorruptionsHurtAsTheirGradeDoesAndNoOtherHurt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            TestWorld.load(level, 130_400 - 40, 130_400 + 40, 3000 - 40, 3000 + 40);
            Villager wearer = villager(level, 130_400.5, 3000.5);
            List<Float> reaching = new ArrayList<>();
            Consumer<LivingIncomingDamageEvent> listener = event -> { if (event.getEntity() == wearer) reaching.add(event.getAmount()); };
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, LivingIncomingDamageEvent.class, listener);
            try {
                var corruption = ModDamageTypes.corruption(level);
                assertTrue(CorruptionVeilApi.isCorruptionDamage(corruption), "the corruption's hurt");
                assertTrue(CorruptionVeilApi.isCorruptionDamage(ModDamageTypes.conversion(level)), "and its taking a creature at once");
                assertEquals(10, CorruptionVeilApi.damageTaken(wearer, corruption, 10), 1e-6, "unrobed, all of it");
                dress(wearer, Tier.NOVICE);
                assertEquals(10, CorruptionVeilApi.damageTaken(wearer, corruption, 10), 1e-6, "the novice's turns none of it aside");
                dress(wearer, Tier.INTERMEDIATE);
                assertEquals(5, CorruptionVeilApi.damageTaken(wearer, corruption, 10), 1e-6, "the intermediate's half");
                assertEquals(10, CorruptionVeilApi.damageTaken(wearer, level.damageSources().generic(), 10), 1e-6, "and nothing of any other hurt");
                wearer.invulnerableTime = 0;
                assertTrue(wearer.hurt(corruption, 10));
                assertEquals(5, reaching.getLast(), 1e-6, "half of it comes at the wearer, its armour still to take its share");
                dress(wearer, Tier.ADVANCED);
                assertEquals(0, CorruptionVeilApi.damageTaken(wearer, corruption, 10), 1e-6, "the advanced's all of it");
                float health = wearer.getHealth();
                wearer.invulnerableTime = 0;
                assertFalse(wearer.hurt(corruption, 10), "none of it reaches the wearer");
                assertEquals(health, wearer.getHealth());
                wearer.invulnerableTime = 0;
                assertTrue(wearer.hurt(level.damageSources().generic(), 2), "while any other hurt still does");
            } finally {
                NeoForge.EVENT_BUS.unregister(listener);
                wearer.discard();
            }
        });
    }

    @Test void theBladesStrikeSixteenAndThirtyTwoAndBearTheirMarks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ItemStack energy = new ItemStack(ModItems.CORRUPTION_ENERGY_SWORD.get()), arcane = new ItemStack(ModItems.CORRUPTED_ARCANE_SWORD.get());
            assertEquals(ArcaneBladeRules.ENERGY_SWORD_DAMAGE, strikes(energy), 1e-6, "the energy sword, 16");
            assertEquals(ArcaneBladeRules.ARCANE_SWORD_DAMAGE, strikes(arcane), 1e-6, "the arcane sword, 32");
            assertEquals(ArcaneBladeRules.ENERGY_SWORD_DURABILITY, energy.getMaxDamage());
            assertEquals(ArcaneBladeRules.ARCANE_SWORD_DURABILITY, arcane.getMaxDamage());
            assertTrue(energy.is(ArcaneWeapons.MARKED) && !energy.is(ArcaneWeapons.CURSED), "the energy sword: the arcane's mark, and no curse");
            assertTrue(arcane.is(ArcaneWeapons.MARKED) && arcane.is(ArcaneWeapons.CURSED), "the arcane sword: both");
            assertTrue(energy.is(ItemTags.SWORDS) && arcane.is(ItemTags.SWORDS), "swords, as the game's enchantments know them");
            List<ArmorItem> robes = new ArrayList<>();
            ModItems.CORRUPTED_ROBES.forEach(robe -> robes.add(robe.get()));
            ModItems.ROBE_SET.forEach(robe -> robes.add(robe.get()));
            for (ArmorItem robe : robes) {
                TagKey<Item> worn = switch (robe.getType()) {
                    case HELMET -> ItemTags.HEAD_ARMOR;
                    case CHESTPLATE -> ItemTags.CHEST_ARMOR;
                    case LEGGINGS -> ItemTags.LEG_ARMOR;
                    default -> ItemTags.FOOT_ARMOR;
                };
                assertTrue(new ItemStack(robe).is(worn), BuiltInRegistries.ITEM.getKey(robe) + " takes armour enchantments as the armour it is");
            }
            for (EntityType<?> lord : List.of(ModEntities.BAAL.get(), ModEntities.AGARES.get(), ModEntities.ANDROMALIUS.get(), ModEntities.DANTALION.get(),
                    ModEntities.SEERE.get())) {
                assertTrue(lord.is(CorruptionFactionApi.BOSSES), EntityType.getKey(lord) + " is one of the corruption's lords");
                assertTrue(lord.is(Tags.EntityTypes.BOSSES), "and a boss as every mod knows them");
            }
            assertFalse(ModEntities.CORRUPTED_REMNANT.get().is(CorruptionFactionApi.BOSSES), "its creatures are not");
        });
    }

    @Test void theEnergySwordReachesTheCorruptionsLordsAndTheArcaneSwordsCurseIsTurnedAsideByThem(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            TestWorld.load(level, 130_800 - 40, 130_800 + 40, 3000 - 40, 3000 + 40);
            FakePlayer wielder = player(level, "blade_wielder", 0);
            wielder.moveTo(130_800.5, -60, 2995.5, 0, 0);
            Andromalius lord = ModEntities.ANDROMALIUS.get().create(level);
            lord.moveTo(130_800.5, -60, 3000.5, 0, 0);
            lord.setNoAi(true);
            level.addFreshEntity(lord);
            try {
                wielder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.NETHERITE_SWORD));
                assertFalse(lord.hurt(level.damageSources().playerAttack(wielder), 10), "a plain blade does not reach him");
                wielder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CORRUPTION_ENERGY_SWORD.get()));
                float health = lord.getHealth();
                assertTrue(lord.hurt(level.damageSources().playerAttack(wielder), 10), "the energy sword bears the arcane's mark");
                assertTrue(lord.getHealth() < health, "and hurts him");
                lord.invulnerableTime = 0;
                health = lord.getHealth();
                wielder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CORRUPTED_ARCANE_SWORD.get()));
                assertFalse(lord.hurt(level.damageSources().playerAttack(wielder), 10), "the arcane sword bears it too, but he turns its curse aside");
                assertEquals(health, lord.getHealth());
                assertFalse(lord.hasEffect(ModEffects.CORRUPTION_CURSE));
            } finally {
                lord.discard();
            }
        });
    }

    @Test void theArcaneSwordCursesWhatIsNotOfTheCorruptionAndTheEnergySwordCursesNothing(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            TestWorld.load(level, 131_200 - 40, 131_200 + 40, 3000 - 40, 3000 + 40);
            FakePlayer wielder = player(level, "curse_wielder", 0);
            IronGolem cursed = golem(level, 131_200.5, 3000.5), clean = golem(level, 131_204.5, 3000.5);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            remnant.moveTo(131_208.5, -60, 3000.5, 0, 0);
            remnant.setNoAi(true);
            level.addFreshEntity(remnant);
            try {
                wielder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CORRUPTED_ARCANE_SWORD.get()));
                assertTrue(cursed.hurt(level.damageSources().playerAttack(wielder), 10));
                MobEffectInstance curse = cursed.getEffect(ModEffects.CORRUPTION_CURSE);
                assertNotNull(curse, "the arcane sword lays the corruption's curse");
                assertEquals(ArcaneBladeRules.CURSE_LEVEL, curse.getAmplifier());
                assertEquals(ArcaneBladeRules.CURSE_TICKS, curse.getDuration());
                assertTrue(remnant.hurt(level.damageSources().playerAttack(wielder), 10), "it strikes the corruption's creatures");
                assertFalse(remnant.hasEffect(ModEffects.CORRUPTION_CURSE), "but its curse does not touch them");
                wielder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CORRUPTION_ENERGY_SWORD.get()));
                assertTrue(clean.hurt(level.damageSources().playerAttack(wielder), 10));
                assertFalse(clean.hasEffect(ModEffects.CORRUPTION_CURSE), "the energy sword lays none");
            } finally {
                cursed.discard();
                clean.discard();
                remnant.discard();
            }
        });
    }

    @Test void remakingABladeOrARobeKeepsWhatWasSetOnIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Holder<Enchantment> sharpness = enchantment(level, Enchantments.SHARPNESS);
            ItemStack energy = new ItemStack(ModItems.CORRUPTION_ENERGY_SWORD.get());
            energy.enchant(sharpness, 5);
            energy.setDamageValue(100);
            ShapedAlchemyRecipe bench = assertInstanceOf(ShapedAlchemyRecipe.class, recipe(level, "alchemy/corrupted_arcane_sword"));
            AlchemyInput onBench = new AlchemyInput(List.of(energy, new ItemStack(ModItems.RESENTMENT.get(), 3), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
            assertTrue(bench.matches(onBench, level), "the energy sword and resentment, on the bench");
            ItemStack arcane = bench.assemble(onBench, level.registryAccess());
            assertTrue(arcane.is(ModItems.CORRUPTED_ARCANE_SWORD.get()), "make the arcane sword");
            assertEquals(5, arcane.getEnchantmentLevel(sharpness), "keeping its enchantments");
            assertEquals(100, arcane.getDamageValue(), "and its wear");

            AltarRecipe altar = assertInstanceOf(AltarRecipe.class, recipe(level, "altar/corruption_energy_sword"));
            List<ItemStack> pillars = new ArrayList<>(Collections.nCopies(6, ItemStack.EMPTY));
            pillars.set(0, arcane);
            AltarInput atAltar = new AltarInput(pillars, null);
            assertTrue(altar.matches(atAltar, level), "the arcane sword, given up on the altar");
            ItemStack back = altar.assemble(atAltar, level.registryAccess());
            assertTrue(back.is(ModItems.CORRUPTION_ENERGY_SWORD.get()), "is the energy sword again");
            assertEquals(5, back.getEnchantmentLevel(sharpness), "its enchantments still kept");

            Holder<Enchantment> protection = enchantment(level, Enchantments.PROTECTION);
            ItemStack robe = new ItemStack(ModItems.ROBE.get());
            robe.enchant(protection, 3);
            ShapedAlchemyRecipe novice = assertInstanceOf(ShapedAlchemyRecipe.class, recipe(level, "alchemy/novice_corrupted_robe"));
            List<ItemStack> slots = new ArrayList<>(List.of(robe));
            for (int slot = 1; slot < novice.getIngredients().size(); slot++) {
                Ingredient wanted = novice.getIngredients().get(slot);
                slots.add(wanted.isEmpty() ? ItemStack.EMPTY : wanted.getItems()[0].copyWithCount(64));
            }
            AlchemyInput robeOnBench = new AlchemyInput(slots);
            assertTrue(novice.matches(robeOnBench, level), "the robe and what corrupts it, on the bench");
            ItemStack corrupted = novice.assemble(robeOnBench, level.registryAccess());
            assertTrue(corrupted.is(piece(Tier.NOVICE, ArmorItem.Type.CHESTPLATE).getItem()), "make the novice's corrupted robe");
            assertEquals(3, corrupted.getEnchantmentLevel(protection), "keeping its enchantments");
        });
    }

    @Test void everyRobeAndBladeIsMadeAndEveryWorkingBlockOfTheCorruptionAtACraftingTable(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            for (DeferredItem<CorruptedRobeArmorItem> robe : ModItems.CORRUPTED_ROBES)
                assertTrue(recipe(level, "alchemy/" + robe.getId().getPath()).getResultItem(level.registryAccess()).is(robe.get()), robe.getId() + " is made");
            assertTrue(recipe(level, "alchemy/corruption_energy_sword").getResultItem(level.registryAccess()).is(ModItems.CORRUPTION_ENERGY_SWORD.get()));
            for (String block : List.of("corruption_arcane_transmuter", "corruption_bench", "brass_cauldron", "altar_pillar", "altar_offering",
                    "corruption_arcane_lectern", "corruption_arcane_pedestal", "corruption_arcane_charger")) {
                Recipe<?> made = recipe(level, block);
                assertEquals(RecipeType.CRAFTING, made.getType(), block + " at a crafting table");
                assertEquals(block, BuiltInRegistries.ITEM.getKey(made.getResultItem(level.registryAccess()).getItem()).getPath(), block + " makes itself");
            }
        });
    }

    @Test void theCorruptionsPlantsAreTakenWholeByShearsOrSilkTouchAndItsMatterBySilkTouchAlone(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ItemStack shears = new ItemStack(Items.SHEARS), pick = new ItemStack(Items.DIAMOND_PICKAXE), silk = pick.copy();
            silk.enchant(enchantment(level, Enchantments.SILK_TOUCH), 1);
            List<Item> dust = List.of(ModItems.CORRUPTION_DUST.get());
            for (String plant : List.of("malice_grass", "blood_filaments", "whisper_bloom", "eerie", "eldritch")) {
                Item whole = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("corruption", plant)).asItem();
                assertEquals(List.of(whole), drops(level, plant, shears), plant + " to shears");
                assertEquals(List.of(whole), drops(level, plant, silk), plant + " to Silk Touch");
                assertEquals(dust, drops(level, plant, ItemStack.EMPTY), plant + " to anything else, dust as before");
            }
            for (String matter : List.of("corruption", "deactivated_corruption", "malice")) {
                Item whole = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("corruption", matter)).asItem();
                assertEquals(List.of(whole), drops(level, matter, silk), matter + " to Silk Touch");
                assertEquals(dust, drops(level, matter, pick), matter + " to anything else, dust as before");
                assertEquals(dust, drops(level, matter, shears), "shears take only the plants whole");
            }
        });
    }

    @Test void theLivingPlantsLeaveTheirOwnMatterToAPlayersKill(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer killer = player(level, "plant_killer", 0);
            var vine = ModEntities.MALICE_VINE.get().create(level);
            var greed = ModEntities.GREED.get().create(level);
            assertTrue(killed(level, vine, killer).contains(ModItems.MALICE_TENDRIL.get()), "the malice vine its tendril");
            assertTrue(killed(level, greed, killer).contains(ModItems.GREED_HEARTWOOD.get()), "greed its heartwood");
            assertTrue(killed(level, vine, killer).contains(ModItems.CORRUPTION_REMAIN.get()), "and its remains, as before");
            assertFalse(killed(level, vine, null).contains(ModItems.MALICE_TENDRIL.get()), "but its own matter only to a player's kill");
        });
    }
}
