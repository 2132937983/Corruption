package com.miophas.corruption.common.item;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneWeapons;
import com.miophas.corruption.api.corruption.CorruptionCurseApi;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.common.alchemy.AlchemyInput;
import com.miophas.corruption.common.altar.AltarInput;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.energy.EnergyCharging;
import com.miophas.corruption.common.entity.Andromalius;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.energy.EnergyDeviceRules;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The corruption echo alloy's line on a real server: its intermediate and the alloy made at the bench, its block at a
 * crafting table, its gear at the altar keeping what was set on the diamond piece; the gear's weight, the sword's curse
 * and the lords' refusal of it; the armour turning aside the curse's own hurt and nothing else, measured against armoured
 * villagers standing in for players; what the elites and remnants leave to a player's kill; the batteries and the devices
 * that run on a charge; and the alloy's block.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class EchoAlloyServerTest {
    private static final List<String> TOOLS = List.of("sword", "pickaxe", "axe"), ARMOUR = List.of("helmet", "chestplate", "leggings", "boots");

    private static Recipe<?> recipe(ServerLevel level, String path) {
        return level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", path))
                .orElseThrow(() -> new AssertionError("corruption:" + path + " loads")).value();
    }
    private static Item item(String id) { return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id.contains(":") ? id : "corruption:" + id)); }
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
    private static FakePlayer player(ServerLevel level, String name) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        for (EquipmentSlot slot : EquipmentSlot.values()) player.setItemSlot(slot, ItemStack.EMPTY);
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth());
        return player;
    }
    private static Villager villager(ServerLevel level, double x, double z) {
        Villager villager = EntityType.VILLAGER.create(level);
        villager.moveTo(x, -60, z, 0, 0);
        villager.setNoAi(true);
        level.addFreshEntity(villager);
        return villager;
    }
    /** Dresses a wearer in a set of armour by its pieces' ids, head to foot (null for none). */
    private static void dress(LivingEntity wearer, @Nullable List<String> pieces) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int at = 0; at < 4; at++) wearer.setItemSlot(slots[at], pieces == null || pieces.get(at) == null ? ItemStack.EMPTY : new ItemStack(item(pieces.get(at))));
        wearer.tick(); // what is worn counts once the wearer has lived a tick: its armour's attributes are put on then
    }
    private static List<String> set(String material) { return ARMOUR.stream().map(piece -> material + "_" + piece).toList(); }
    /** How much of its health a fresh wearer loses to one hurt of this much from this source. */
    private static float lost(LivingEntity wearer, DamageSource source, float amount) {
        wearer.setHealth(wearer.getMaxHealth());
        wearer.invulnerableTime = 0;
        wearer.hurt(source, amount);
        float lost = wearer.getMaxHealth() - wearer.getHealth();
        wearer.setHealth(wearer.getMaxHealth());
        wearer.invulnerableTime = 0;
        return lost;
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
    private static List<ItemStack> slots(ItemStack... stacks) {
        List<ItemStack> slots = new ArrayList<>(List.of(stacks));
        while (slots.size() < 5) slots.add(ItemStack.EMPTY);
        return slots;
    }

    @Test void theAlloyIsMadeAtTheBenchFromAResonantCrystalAndNetheritesScrapAndGoldAndItsBlockAtACraftingTable(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Recipe<AlchemyInput> crystal = cast(recipe(level, "alchemy/resonant_corruption_crystal"));
            AlchemyInput crystals = new AlchemyInput(slots(new ItemStack(ModItems.CORRUPTION_CRYSTAL.get(), 4), new ItemStack(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get(), 2)));
            assertTrue(crystal.matches(crystals, level), "four corruption crystals and two ancient ones");
            assertTrue(crystal.assemble(crystals, level.registryAccess()).is(ModItems.RESONANT_CORRUPTION_CRYSTAL.get()), "ring as one");
            assertFalse(crystal.matches(new AlchemyInput(slots(new ItemStack(ModItems.CORRUPTION_CRYSTAL.get(), 4))), level), "not the new alone");

            Recipe<AlchemyInput> alloy = cast(recipe(level, "alchemy/corruption_echo_alloy"));
            AlchemyInput netherite = new AlchemyInput(slots(new ItemStack(ModItems.RESONANT_CORRUPTION_CRYSTAL.get()), new ItemStack(Items.NETHERITE_SCRAP, 4),
                    new ItemStack(Items.GOLD_INGOT, 4)));
            assertTrue(alloy.matches(netherite, level), "the resonant crystal with what a netherite ingot is made of");
            assertTrue(alloy.assemble(netherite, level.registryAccess()).is(ModItems.CORRUPTION_ECHO_ALLOY.get()));
            assertFalse(alloy.matches(new AlchemyInput(slots(new ItemStack(Items.NETHERITE_SCRAP, 4), new ItemStack(Items.GOLD_INGOT, 4))), level),
                    "netherite's own makings are not enough");

            assertTrue(recipe(level, "corruption_echo_alloy_block").getResultItem(level.registryAccess()).is(ModItems.CORRUPTION_ECHO_ALLOY_BLOCK.get()));
            ItemStack back = recipe(level, "corruption_echo_alloy_from_block").getResultItem(level.registryAccess());
            assertTrue(back.is(ModItems.CORRUPTION_ECHO_ALLOY.get()) && back.getCount() == 9, "and back to nine ingots");
            ItemStack shards = recipe(level, "brass_cooking/void_tainted_shard").getResultItem(level.registryAccess());
            assertTrue(shards.is(ModItems.VOID_TAINTED_SHARD.get()) && shards.getCount() == 2, "the shards at the cauldron, two at a time");
        });
    }

    @Test void eachPieceOfGearIsMadeAtTheAltarFromItsDiamondOneKeepingWhatWasSetOnIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Holder<Enchantment> unbreaking = enchantment(level, Enchantments.UNBREAKING);
            for (String piece : concat(TOOLS, ARMOUR)) {
                AltarRecipe altar = assertInstanceOf(AltarRecipe.class, recipe(level, "altar/corruption_echo_alloy_" + piece));
                ItemStack diamond = new ItemStack(item("minecraft:diamond_" + piece));
                diamond.enchant(unbreaking, 3);
                diamond.setDamageValue(40);
                Item with = TOOLS.contains(piece) ? ModItems.VOID_TAINTED_SHARD.get() : ModItems.CHITIN_PLATE.get();
                List<ItemStack> pillars = new ArrayList<>(Collections.nCopies(6, ItemStack.EMPTY));
                pillars.set(0, diamond);
                pillars.set(2, new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY.get()));
                pillars.set(4, new ItemStack(with));
                AltarInput input = new AltarInput(pillars, null);
                assertTrue(altar.matches(input, level), piece + ": the diamond one, the alloy and " + with);
                ItemStack made = altar.assemble(input, level.registryAccess());
                assertTrue(made.is(item("corruption_echo_alloy_" + piece)), piece);
                assertEquals(3, made.getEnchantmentLevel(unbreaking), piece + " keeps its enchantments");
                assertEquals(40, made.getDamageValue(), piece + " and its wear");
                pillars.set(2, ItemStack.EMPTY);
                assertFalse(altar.matches(new AltarInput(pillars, null), level), piece + ": not without the alloy");
            }
        });
    }

    @Test void theToolsOutdoNetheritesAndTheSwordLaysTheCurseButTheLordsTurnItAside(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Map<String, Item> netherite = Map.of("sword", Items.NETHERITE_SWORD, "pickaxe", Items.NETHERITE_PICKAXE, "axe", Items.NETHERITE_AXE);
            for (String tool : TOOLS) {
                ItemStack echo = new ItemStack(item("corruption_echo_alloy_" + tool));
                assertTrue(strikes(echo) > strikes(new ItemStack(netherite.get(tool))), tool + " strikes harder than netherite's: " + strikes(echo));
                assertTrue(echo.getMaxDamage() > new ItemStack(netherite.get(tool)).getMaxDamage(), tool + " lasts longer");
                assertTrue(echo.has(DataComponents.FIRE_RESISTANT), tool + " does not burn");
                assertTrue(echo.getItem().isValidRepairItem(echo, new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY.get())), tool + " is mended with the alloy");
            }
            assertEquals(9, strikes(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_SWORD.get())), 1e-6, "the sword strikes 9, netherite's 8");
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_SWORD.get()).is(ItemTags.SWORDS));
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_PICKAXE.get()).is(ItemTags.PICKAXES));
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_AXE.get()).is(ItemTags.AXES));
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_PICKAXE.get()).isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState()), "it digs obsidian");
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_SWORD.get()).is(ArcaneWeapons.CURSED), "the sword carries the curse");
            assertFalse(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_AXE.get()).is(ArcaneWeapons.CURSED), "the axe does not");

            TestWorld.load(level, 140_800 - 40, 140_800 + 40, 3000 - 40, 3000 + 40);
            FakePlayer wielder = player(level, "echo_wielder");
            wielder.moveTo(140_800.5, -60, 2995.5, 0, 0);
            wielder.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY_SWORD.get()));
            Villager struck = villager(level, 140_802.5, 2995.5);
            Andromalius lord = ModEntities.ANDROMALIUS.get().create(level);
            lord.moveTo(140_800.5, -60, 3000.5, 0, 0);
            lord.setNoAi(true);
            level.addFreshEntity(lord);
            try {
                assertTrue(struck.hurt(level.damageSources().playerAttack(wielder), 9), "it lands on a villager");
                assertTrue(struck.hasEffect(ModEffects.CORRUPTION_CURSE), "and lays the curse on it");
                float health = lord.getHealth();
                assertFalse(lord.hurt(level.damageSources().playerAttack(wielder), 9), "a lord of the corruption turns its curse aside");
                assertEquals(health, lord.getHealth());
            } finally {
                lord.discard();
                struck.discard();
            }
        });
    }

    @Test void theArmourTurnsAsideTheCursesOwnHurtAndGuardsAgainstAllElseAsNetheriteDoes(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            TestWorld.load(level, 141_800 - 40, 141_800 + 40, 3000 - 40, 3000 + 40);
            Villager wearer = villager(level, 141_800.5, 3000.5);
            Villager boss = villager(level, 141_805.5, 3000.5);
            try {
                float pulse = wearer.getMaxHealth() * .15F;
                DamageSource curse = CorruptionCurseApi.damage(level);
                assertTrue(CorruptionCurseApi.isCurse(curse));
                assertTrue(curse.is(ModDamageTypes.CORRUPTION), "the curse still hurts with the corruption's own damage type");
                assertFalse(CorruptionCurseApi.isCurse(ModDamageTypes.corruption(level)), "the ground's hurt is not the curse");
                assertFalse(CorruptionCurseApi.isCurse(ModDamageTypes.corruption(level, boss)), "nor a creature's");

                dress(wearer, null);
                float bare = lost(wearer, curse, pulse);
                dress(wearer, set("minecraft:diamond"));
                float diamond = lost(wearer, curse, pulse);
                dress(wearer, set("minecraft:netherite"));
                float netherite = lost(wearer, curse, pulse), netheriteSkill = lost(wearer, ModDamageTypes.corruption(level, boss), 8),
                        netheriteGround = lost(wearer, ModDamageTypes.corruption(level), pulse), netheriteBlow = lost(wearer, level.damageSources().mobAttack(boss), 8);
                dress(wearer, set("corruption_echo_alloy"));
                float echo = lost(wearer, curse, pulse), echoSkill = lost(wearer, ModDamageTypes.corruption(level, boss), 8),
                        echoGround = lost(wearer, ModDamageTypes.corruption(level), pulse), echoBlow = lost(wearer, level.damageSources().mobAttack(boss), 8);
                assertEquals(0, echo, 1e-6, "the whole set: none of a pulse of the curse");
                assertEquals(netheriteSkill, echoSkill, 1e-4, "a creature's corruption, as netherite guards against it");
                assertEquals(netheriteGround, echoGround, 1e-4, "the corrupted ground's, the same");
                assertEquals(netheriteBlow, echoBlow, 1e-4, "a plain blow, the same");

                assertTrue(diamond < bare && netherite < bare, "armour tempers the curse as it does any corruption (the stand-in's armour counts)");
                List<String> chest = new ArrayList<>(Collections.nCopies(4, (String) null));
                chest.set(1, "minecraft:netherite_chestplate");
                dress(wearer, chest);
                float netheriteChest = lost(wearer, curse, pulse * .6F);
                chest.set(1, "corruption:corruption_echo_alloy_chestplate");
                dress(wearer, chest);
                float echoChest = lost(wearer, curse, pulse);
                assertEquals(netheriteChest, echoChest, 1e-4, "a chestplate alone turns aside two fifths of the pulse, then guards as netherite's");

                // The curse as it pulses on the wearer, the effect's own tick.
                dress(wearer, set("corruption_echo_alloy"));
                wearer.setHealth(wearer.getMaxHealth());
                wearer.invulnerableTime = 0;
                ModEffects.CORRUPTION_CURSE.value().applyEffectTick(wearer, 0);
                assertEquals(wearer.getMaxHealth(), wearer.getHealth(), 1e-6, "the curse's pulse does not land on the whole set");
                dress(wearer, null);
                wearer.invulnerableTime = 0;
                ModEffects.CORRUPTION_CURSE.value().applyEffectTick(wearer, 0);
                assertEquals(wearer.getMaxHealth() - pulse, wearer.getHealth(), 1e-4, "and does, unarmoured");
                System.out.printf("MEASURE a pulse of Curse I on a 20-health stand-in: bare %.2f, diamond %.2f, netherite %.2f, echo alloy %.2f;"
                        + " a creature's 8 of corruption: netherite %.2f, echo alloy %.2f%n", bare, diamond, netherite, echo, netheriteSkill, echoSkill);
            } finally {
                wearer.discard();
                boss.discard();
            }
        });
    }

    @Test void theElitesAndRemnantsLeaveTheirMatterToAPlayersKill(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer killer = player(level, "echo_killer");
            Map<String, List<String>> drops = Map.of(
                    "mutated_sinew", List.of("corrupted_brute", "corrupted_howler", "corrupted_blighthound", "corrupted_aberration"),
                    "bile_sac", List.of("corrupted_bloater", "corrupted_mortar"),
                    "neural_cluster", List.of("corrupted_mindweaver", "corrupted_screecher", "corrupted_gazer"),
                    "chitin_plate", List.of("corrupted_crawler", "corrupted_hivemother", "corrupted_lurker", "corrupted_stalker"));
            for (var entry : drops.entrySet())
                for (String elite : entry.getValue()) {
                    LivingEntity creature = (LivingEntity) BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath("corruption", elite)).create(level);
                    List<Item> left = killed(level, creature, killer);
                    long count = left.stream().filter(it -> it == item(entry.getKey())).count();
                    assertTrue(count >= 1, elite + " leaves " + entry.getKey() + " to a player: " + left);
                    assertTrue(left.contains(ModItems.CORRUPTION_REMAIN.get()), elite + " still leaves the remnant's remains");
                    assertFalse(killed(level, creature, null).contains(item(entry.getKey())), elite + ": only to a player's kill");
                }
            LivingEntity remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            int rolls = 4000, marrow = 0;
            for (int roll = 0; roll < rolls; roll++) if (killed(level, remnant, killer).contains(ModItems.REMNANT_MARROW.get())) marrow++;
            double rate = (double) marrow / rolls;
            assertTrue(rate > .07 && rate < .13, "a remnant leaves its marrow about one time in ten: " + rate);
            for (int roll = 0; roll < 200; roll++) assertFalse(killed(level, remnant, null).contains(ModItems.REMNANT_MARROW.get()), "and only to a player");
            System.out.printf("MEASURE remnant marrow, %d player kills without looting: %.3f%n", rolls, rate);
        });
    }

    @Test void theBatteriesCarryTheirChargeAndTheDevicesSpendTheirsOnTheirWork(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptionEnergyCarrier sac = CorruptionEnergyCarrier.of(new ItemStack(ModItems.CHARGED_BILE_SAC.get())), cell = CorruptionEnergyCarrier.of(new ItemStack(ModItems.ECHO_CELL.get()));
            assertNotNull(sac);
            assertNotNull(cell);
            assertEquals(CorruptionEnergyApi.kilo(15), sac.capacity());
            assertTrue(sac.consumed(), "the bile sac is spent once");
            assertEquals(CorruptionEnergyApi.kilo(100), cell.capacity());
            assertFalse(cell.consumed(), "the cell is kept when empty");
            ItemStack charged = recipe(level, "arcane_transmutation/charged_bile_sac").getResultItem(level.registryAccess());
            assertEquals(CorruptionEnergyApi.kilo(15), CorruptionEnergyCarrier.carried(charged), "the transmuter charges a bile sac full");
            ItemStack filled = recipe(level, "arcane_transmutation/echo_cell_filling").getResultItem(level.registryAccess());
            assertEquals(CorruptionEnergyApi.kilo(100), CorruptionEnergyCarrier.carried(filled), "and fills an echo cell");

            TestWorld.load(level, 142_800 - 40, 142_800 + 40, 3000 - 40, 3000 + 40);
            FakePlayer holder = player(level, "echo_lantern");
            holder.moveTo(142_800.5, -60, 3000.5, 0, 0);
            ItemStack lantern = new ItemStack(ModItems.CORRUPTED_LANTERN.get());
            assertTrue(EnergyCharging.chargeable(lantern), "an empty lantern goes on the charger");
            assertFalse(EnergyCharging.chargeable(CorruptionEnergyTool.charged(lantern, EnergyDeviceRules.LANTERN.tool().capacity())), "a full one does not");
            var remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            remnant.moveTo(142_810.5, -60, 3000.5, 0, 0);
            remnant.setNoAi(true);
            level.addFreshEntity(remnant);
            Villager villager = villager(level, 142_806.5, 3000.5);
            try {
                holder.setItemInHand(InteractionHand.OFF_HAND, lantern);
                assertFalse(ModItems.CORRUPTED_LANTERN.get().burn(level, holder, lantern), "an empty lantern does not burn");
                assertFalse(holder.hasEffect(MobEffects.NIGHT_VISION));
                lantern.set(CorruptionEnergyCarrier.ENERGY.get(), 100L);
                assertTrue(ModItems.CORRUPTED_LANTERN.get().burn(level, holder, lantern), "a charged one burns");
                assertEquals(100 - EnergyDeviceRules.LANTERN.cost(), CorruptionEnergyTool.held(lantern), "spending its cost");
                assertTrue(holder.hasEffect(MobEffects.NIGHT_VISION), "its holder sees in the dark");
                assertTrue(remnant.hasEffect(MobEffects.GLOWING), "the corruption's creature about them glows");
                assertFalse(villager.hasEffect(MobEffects.GLOWING), "and nothing else does");

                ItemStack injector = new ItemStack(ModItems.ICHOR_INJECTOR.get());
                assertTrue(EnergyCharging.chargeable(injector), "an empty injector goes on the charger");
                holder.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, 200));
                holder.setHealth(10);
                assertFalse(ModItems.ICHOR_INJECTOR.get().inject(holder, injector, false), "an empty injector gives nothing");
                assertTrue(holder.hasEffect(ModEffects.CORRUPTION_CURSE));
                injector = CorruptionEnergyTool.charged(injector, EnergyDeviceRules.INJECTOR.tool().capacity());
                assertTrue(ModItems.ICHOR_INJECTOR.get().inject(holder, injector, false), "a charged one doses");
                assertFalse(holder.hasEffect(ModEffects.CORRUPTION_CURSE), "taking the curse off");
                assertEquals(10 + EnergyDeviceRules.INJECTOR.heal(), holder.getHealth(), 1e-4, "and healing three hearts");
                assertEquals(EnergyDeviceRules.INJECTOR.tool().capacity() - EnergyDeviceRules.INJECTOR.cost(), CorruptionEnergyTool.held(injector));
                assertTrue(holder.getCooldowns().isOnCooldown(ModItems.ICHOR_INJECTOR.get()), "then it rests");
            } finally {
                remnant.discard();
                villager.discard();
            }
        });
    }

    @Test void theAlloysBlockIsMinedWithDiamondAndIsABeaconsBaseAndTheInfectionLeavesIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockState block = ModBlocks.CORRUPTION_ECHO_ALLOY_BLOCK.get().defaultBlockState();
            assertTrue(block.is(BlockTags.MINEABLE_WITH_PICKAXE));
            assertTrue(block.is(BlockTags.NEEDS_DIAMOND_TOOL));
            assertTrue(block.is(BlockTags.BEACON_BASE_BLOCKS), "a beacon stands on it");
            assertTrue(block.is(CorruptionConversions.IMMUNE), "the infection does not take it");
            assertEquals(Blocks.NETHERITE_BLOCK.defaultBlockState().getDestroySpeed(level, BlockPos.ZERO), block.getDestroySpeed(level, BlockPos.ZERO), "as hard as netherite");
            assertFalse(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(block), "iron does not mine it");
            assertTrue(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(block), "diamond does");
            assertTrue(Block.getDrops(block, level, BlockPos.ZERO, null, null, new ItemStack(Items.DIAMOND_PICKAXE)).stream()
                    .anyMatch(stack -> stack.is(ModItems.CORRUPTION_ECHO_ALLOY_BLOCK.get())), "and it gives itself up");
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY.get()).is(ItemTags.BEACON_PAYMENT_ITEMS), "the alloy pays a beacon");
            assertTrue(new ItemStack(ModItems.CORRUPTION_ECHO_ALLOY.get()).is(TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/corruption_echo_alloy"))));
            for (Item fireproof : List.of(ModItems.CORRUPTION_ECHO_ALLOY.get(), ModItems.CORRUPTION_ECHO_ALLOY_BLOCK.get(), ModItems.CORRUPTION_ECHO_ALLOY_HELMET.get()))
                assertTrue(new ItemStack(fireproof).has(DataComponents.FIRE_RESISTANT), fireproof + " does not burn");
        });
    }

    @SuppressWarnings("unchecked")
    private static Recipe<AlchemyInput> cast(Recipe<?> recipe) { return (Recipe<AlchemyInput>) recipe; }

    private static List<String> concat(List<String> a, List<String> b) {
        List<String> both = new ArrayList<>(a);
        both.addAll(b);
        return both;
    }
}
