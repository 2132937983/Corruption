package com.miophas.corruption.common.winnow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.winnow.LeafWinnowApi;
import com.miophas.corruption.api.winnow.LeafWinnowDrop;
import com.miophas.corruption.api.winnow.LeafWinnowTable;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * Loads the winnowing tables from {@code data/<namespace>/leaf_winnowing/<name>.json}:
 *
 * <pre>{@code
 * {
 *   "block": "minecraft:oak_leaves",
 *   "drops": [ { "item": "minecraft:apple", "chance": 0.5 } ]
 * }
 * }</pre>
 *
 * <p>{@code chance} defaults to 1, {@code min} to 1 and {@code max} to {@code min}.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class LeafWinnowData {
    /** Data pack folder holding one winnowing table per file. */
    public static final String DIRECTORY = "leaf_winnowing";
    private static final Gson GSON = new GsonBuilder().setLenient().create();

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new Loader());
    }

    private static final class Loader extends SimpleJsonResourceReloadListener {
        private Loader() { super(GSON, DIRECTORY); }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resources, ProfilerFiller profiler) {
            LeafWinnowApi.clear();
            for (Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
                try {
                    LeafWinnowTable table = parse(entry.getKey(), GsonHelper.convertToJsonObject(entry.getValue(), "winnowing table"));
                    if (table != null) LeafWinnowApi.register(table);
                } catch (RuntimeException failure) {
                    LogUtils.getLogger().error("Skipping leaf winnowing table {}", entry.getKey(), failure);
                }
            }
        }

        private static LeafWinnowTable parse(ResourceLocation id, JsonObject json) {
            boolean tagged = json.has("tag");
            String name = GsonHelper.getAsString(json, tagged ? "tag" : "block");
            if (ResourceLocation.tryParse(name) == null) {
                LogUtils.getLogger().error("Leaf winnowing table {} names an invalid {} {}", id,
                        tagged ? "tag" : "block", name);
                return null;
            }
            JsonArray entries = GsonHelper.getAsJsonArray(json, "drops", new JsonArray());
            List<LeafWinnowDrop> drops = new ArrayList<>(entries.size());
            for (JsonElement element : entries) drops.add(parseDrop(id, GsonHelper.convertToJsonObject(element, "drop")));
            return new LeafWinnowTable(tagged ? "#" + name : name, drops);
        }

        private static LeafWinnowDrop parseDrop(ResourceLocation id, JsonObject json) {
            String item = GsonHelper.getAsString(json, "item");
            if (ResourceLocation.tryParse(item) == null)
                throw new JsonSyntaxException("Leaf winnowing table " + id + " names an invalid item " + item);
            int min = Math.max(1, GsonHelper.getAsInt(json, "min", 1));
            return new LeafWinnowDrop(item, GsonHelper.getAsFloat(json, "chance", 1F),
                    min, GsonHelper.getAsInt(json, "max", min));
        }
    }

    private LeafWinnowData() {}
}
