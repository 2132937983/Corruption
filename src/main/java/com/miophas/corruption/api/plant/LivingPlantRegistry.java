package com.miophas.corruption.api.plant;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

/** Open species identities. Register the same definitions on both logical sides during mod construction. */
public final class LivingPlantRegistry {
    public record Species(LivingPlantDefinition definition, boolean reserveCanopy) {
        public Species { Objects.requireNonNull(definition); }
    }
    public static final ResourceLocation MALICE_VINE = ResourceLocation.parse("corruption:malice_vine");
    public static final ResourceLocation GREED = ResourceLocation.parse("corruption:greed");
    private static final Map<ResourceLocation, Species> SPECIES = new ConcurrentHashMap<>();
    private static volatile double maximumHeight = 10, maximumWidth = 3;
    static {
        register(MALICE_VINE, new Species(LivingPlantApi.definition(LivingPlantKind.MALICE_VINE), false));
        register(GREED, new Species(LivingPlantApi.definition(LivingPlantKind.GREED), true));
    }
    public static synchronized void register(ResourceLocation id, Species species) {
        if (SPECIES.putIfAbsent(Objects.requireNonNull(id), Objects.requireNonNull(species)) != null)
            throw new IllegalStateException("Living plant already registered: " + id);
        maximumHeight = Math.max(maximumHeight, species.definition.height(species.definition.stages() - 1));
        maximumWidth = Math.max(maximumWidth, species.definition.width(species.definition.stages() - 1));
    }
    public static Species species(ResourceLocation id) {
        Species species = SPECIES.get(id);
        if (species == null) throw new IllegalArgumentException("Unknown living plant: " + id);
        return species;
    }
    public static ResourceLocation id(LivingPlantKind legacy) { return legacy == LivingPlantKind.GREED ? GREED : MALICE_VINE; }
    /** Search bounds also cover taller or wider add-on species. */
    public static double maximumHeight() { return maximumHeight; }
    public static double maximumWidth() { return maximumWidth; }
    private LivingPlantRegistry() {}
}
