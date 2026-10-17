package com.miophas.corruption.common.item;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The lords' relics: one for each of the corruption's lords, left by that lord every time it falls and by no other;
 * made by nothing else; glinting, proof against fire, in the item tag corruption:boss_relics; each drawn, and named and
 * described in both languages.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class BossRelicsServerTest {
    private static final List<String> IDS = List.of("baal_horn", "agares_rimeheart", "andromalius_scale", "dantalion_mirror_shard", "seere_pinion", "decarabia_star",
            "belial_halo", "amdusias_horn");

    /** Each lord, and the relic it leaves. */
    private static Map<EntityType<?>, Item> lords() {
        Map<EntityType<?>, Item> lords = new LinkedHashMap<>();
        lords.put(ModEntities.BAAL.get(), ModItems.BAAL_HORN.get());
        lords.put(ModEntities.AGARES.get(), ModItems.AGARES_RIMEHEART.get());
        lords.put(ModEntities.ANDROMALIUS.get(), ModItems.ANDROMALIUS_SCALE.get());
        lords.put(ModEntities.DANTALION.get(), ModItems.DANTALION_MIRROR_SHARD.get());
        lords.put(ModEntities.SEERE.get(), ModItems.SEERE_PINION.get());
        lords.put(ModEntities.DECARABIA.get(), ModItems.DECARABIA_STAR.get());
        lords.put(ModEntities.BELIAL.get(), ModItems.BELIAL_HALO.get());
        lords.put(ModEntities.AMDUSIAS.get(), ModItems.AMDUSIAS_HORN.get());
        return lords;
    }

    private static Set<Item> relics() { return ModItems.RELICS.stream().map(DeferredItem::get).collect(Collectors.toSet()); }

    @Test void everyLordLeavesItsOwnRelicEachTimeItFallsAndNoOtherLordsRelic(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            assertEquals(lords().keySet(), BuiltInRegistries.ENTITY_TYPE.getTag(CorruptionFactionApi.BOSSES).orElseThrow().stream().map(Holder::value)
                    .collect(Collectors.toSet()), "every lord of the corruption has a relic");
            // What these tables leave does not hang on who fell, so a stand-in will do to roll them.
            Villager fallen = EntityType.VILLAGER.create(level);
            fallen.moveTo(0, -60, 0);
            for (var lord : lords().entrySet()) {
                LootTable table = server.reloadableRegistries().getLootTable(lord.getKey().getDefaultLootTable());
                assertNotSame(LootTable.EMPTY, table, lord.getKey() + " leaves something behind");
                for (int roll = 0; roll < 16; roll++) {
                    LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, fallen)
                            .withParameter(LootContextParams.ORIGIN, fallen.position()).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                            .create(LootContextParamSets.ENTITY);
                    List<ItemStack> left = table.getRandomItems(params);
                    assertEquals(1, left.stream().filter(stack -> stack.is(lord.getValue())).mapToInt(ItemStack::getCount).sum(), lord.getKey() + " leaves its relic, one");
                    assertTrue(left.stream().noneMatch(stack -> relics().contains(stack.getItem()) && !stack.is(lord.getValue())), "and no other lord's");
                }
            }
        });
    }

    @Test void theRelicsGlintDoNotBurnAndAreTaggedAndNothingMakesThem(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Set<Item> tagged = BuiltInRegistries.ITEM.getTag(CorruptionFactionApi.RELICS).orElseThrow().stream().map(Holder::value).collect(Collectors.toSet());
            assertEquals(Set.copyOf(lords().values()), tagged, "the tag holds the relics, and only them");
            assertEquals(tagged, relics(), "and they are the relics the mod keeps");
            for (Item relic : tagged) {
                ItemStack stack = new ItemStack(relic);
                assertInstanceOf(BossRelicItem.class, relic);
                assertTrue(stack.has(DataComponents.FIRE_RESISTANT), relic + " does not burn");
                assertTrue(stack.hasFoil(), relic + " glints");
                assertEquals(Rarity.EPIC, stack.getRarity());
            }
            for (var recipe : level.getRecipeManager().getRecipes())
                assertFalse(tagged.contains(recipe.value().getResultItem(level.registryAccess()).getItem()), recipe.id() + " makes a relic");
        });
    }

    @Test void eachIsDrawnNamedAndDescribedInBothLanguages() throws Exception {
        JsonObject en = json("/assets/corruption/lang/en_us.json"), zh = json("/assets/corruption/lang/zh_cn.json");
        for (String id : IDS) {
            BufferedImage texture = image("/assets/corruption/textures/item/" + id + ".png");
            assertEquals(16, texture.getWidth(), id);
            assertEquals(16, texture.getHeight(), id);
            int drawn = 0;
            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) if ((texture.getRGB(x, y) >>> 24) != 0) drawn++;
            assertTrue(drawn > 30, id + " is drawn: " + drawn);
            assertEquals("corruption:item/" + id, json("/assets/corruption/models/item/" + id + ".json").getAsJsonObject("textures").get("layer0").getAsString());
            for (JsonObject lang : List.of(en, zh)) {
                assertTrue(lang.has("item.corruption." + id), id + " is named");
                assertTrue(lang.has("item.corruption." + id + ".desc"), id + " says what it is");
            }
        }
        for (String key : List.of("tooltip.corruption.boss_relic", "tooltip.corruption.boss_relic_gathered"))
            for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has(key), key);
        assertEquals(IDS, ModItems.RELICS.stream().map(relic -> relic.getId().getPath()).toList(), "in the order their lords are met");
    }

    private static JsonObject json(String path) throws Exception {
        try (InputStream in = BossRelicsServerTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = BossRelicsServerTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
