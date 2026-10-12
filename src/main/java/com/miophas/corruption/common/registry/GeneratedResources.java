package com.miophas.corruption.common.registry;

import com.google.gson.Gson;
import com.miophas.corruption.Corruption;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/** Tiny generated JSON resources; block textures remain in the original packs and are composed on reload. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class GeneratedResources extends AbstractPackResources {
    private static final Gson JSON = new Gson();
    private final PackType type;
    private final Map<ResourceLocation, byte[]> files = new HashMap<>();

    public GeneratedResources(PackLocationInfo location, PackType type) {
        super(location); this.type = type;
        var ids = GeneratedCorruption.blocks().stream().map(block -> BuiltInRegistries.BLOCK.getKey(block).toString()).sorted().toList();
        if (type == PackType.CLIENT_RESOURCES) {
            for (String id : ids) {
                ResourceLocation key = ResourceLocation.parse(id);
                put("corruption", "blockstates/" + key.getPath() + ".json", Map.of("variants", Map.of("", Map.of("model", "minecraft:block/stone"))));
                put("corruption", "models/item/" + key.getPath() + ".json", Map.of("parent", "minecraft:block/stone"));
            }
        } else {
            for (String tool : new String[] {"axe", "pickaxe", "hoe"}) put("minecraft", "tags/block/mineable/" + tool + ".json", Map.of("replace", false, "values", ids));
            put("minecraft", "tags/item/non_flammable_wood.json", Map.of("replace", false, "values", ids));
        }
    }
    private void put(String namespace, String path, Object data) { files.put(ResourceLocation.fromNamespaceAndPath(namespace, path), JSON.toJson(data).getBytes(StandardCharsets.UTF_8)); }
    @Override public IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length != 1 || !elements[0].equals("pack.mcmeta")) return null;
        byte[] metadata = JSON.toJson(Map.of("pack", Map.of("pack_format", SharedConstants.getCurrentVersion().getPackVersion(type), "description", "Generated corruption variants"))).getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(metadata);
    }
    @Override public IoSupplier<InputStream> getResource(PackType requested, ResourceLocation location) {
        byte[] data = requested == type ? files.get(location) : null;
        return data == null ? null : () -> new ByteArrayInputStream(data);
    }
    @Override public void listResources(PackType requested, String namespace, String path, ResourceOutput output) {
        if (requested == type) files.forEach((id, data) -> {
            if (id.getNamespace().equals(namespace) && id.getPath().startsWith(path + "/")) output.accept(id, () -> new ByteArrayInputStream(data));
        });
    }
    @Override public Set<String> getNamespaces(PackType requested) { return requested != type ? Set.of() : type == PackType.CLIENT_RESOURCES ? Set.of("corruption") : Set.of("minecraft"); }
    @Override public void close() {}

    @SubscribeEvent public static void find(AddPackFindersEvent event) {
        var location = new PackLocationInfo("corruption_generated", Component.literal("Corruption generated variants"), PackSource.BUILT_IN, Optional.empty());
        event.addRepositorySource(consumer -> {
            var pack = Pack.readMetaAndCreate(location, new Pack.ResourcesSupplier() {
                @Override public PackResources openPrimary(PackLocationInfo info) { return new GeneratedResources(info, event.getPackType()); }
                @Override public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) { return openPrimary(info); }
            }, event.getPackType(), new PackSelectionConfig(true, Pack.Position.TOP, true));
            if (pack != null) consumer.accept(pack);
        });
    }
}
