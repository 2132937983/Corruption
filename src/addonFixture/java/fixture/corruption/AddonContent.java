package fixture.corruption;

import com.miophas.corruption.api.altar.AltarRituals;
import com.miophas.corruption.api.arcane.WorldVisuals;
import com.miophas.corruption.api.corruption.RemnantExtensions;
import com.miophas.corruption.api.corruption.RemnantSpawns;
import com.miophas.corruption.api.corruption.PlayerCorruptionChanges;
import com.miophas.corruption.api.demon.GoeticDemon;
import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.api.elite.CorruptionElite;
import com.miophas.corruption.api.infection.InfectionExtensions;
import com.miophas.corruption.api.plant.AnchoredLivingPlant;
import com.miophas.corruption.api.plant.LivingPlantDefinition;
import com.miophas.corruption.api.plant.LivingPlantRegistry;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.demon.skill.*;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Compiled against the built mod JAR. No common implementation imports are permitted in this source set. */
public final class AddonContent {
    public static final ResourceLocation PLANT = id("thorn_tree"), INFECTION = id("obsidian_infection"), ABILITY = id("pulse");
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("addon_fixture", path); }

    public static void registerPlant() {
        LivingPlantRegistry.register(PLANT, new LivingPlantRegistry.Species(
                new LivingPlantDefinition(5, 2, 2, 1, .5F, 40, false, false, false), true));
    }
    public static void registerInfection() {
        InfectionExtensions.registerSource(INFECTION, state -> state.is(Blocks.OBSIDIAN), state -> true);
        InfectionExtensions.registerConversion(INFECTION, 100, new InfectionExtensions.Conversion() {
            public boolean matches(net.minecraft.world.level.BlockGetter level, BlockPos pos, BlockState state) { return state.is(Blocks.GLASS); }
            public BlockState convert(net.minecraft.world.level.BlockGetter level, BlockPos pos, BlockState state) { return Blocks.OBSIDIAN.defaultBlockState(); }
        });
    }
    public static List<BossSkill<Mob>> skills() {
        SkillTiming timing = new SkillTiming(2, 20, 10);
        return List.of(
                BossSkillApi.fireball(new FireballRules(timing, 0, 64, 1, 1, 1, 1, 2, 1, 0, 0, 0, 0), 0x450620, 0x9B2342),
                BossSkillApi.hands(new HandRules(timing, 0, 64, 1, 2, 3, 3, 3, 3, 2, 2, .2), 0x450620, 0x9B2342),
                BossSkillApi.eliteCall(new EliteCallRules(timing, 0, 64, 1, 2, 3), 0x450620, 0x9B2342),
                BossSkillApi.drakeRush(new DrakeRushRules(timing, 0, 64, 3, 8, 2, 3, 1, 1, 2, .2), 0x450620, 0x9B2342),
                BossSkillApi.swoop(new FlyingStrikeRules(timing, 64, .5), 0, 0));
    }
    public static void visual(ServerLevel level, Vec3 position) {
        WorldVisuals.show(level, position, new SpellAction.Visual("corruption:magic_circle", 2, 1, 20, 0x450620, 0x9B2342, 0, ""), 0, false);
    }
    public static Optional<Mob> remnant(LivingEntity source) { return RemnantSpawns.copy(source); }
    public static int corruptPlayer(net.minecraft.world.entity.player.Player player, int amount) { return PlayerCorruptionChanges.raise(player, amount); }
    public static void registerAbility(Runnable removed) {
        RemnantExtensions.register(ABILITY, source -> source.equals(ResourceLocation.parse("minecraft:cow")), context -> new RemnantExtensions.Ability() {
            public void tick() { context.data().putInt("pulses", context.data().getInt("pulses") + 1); }
            public void removed() { removed.run(); }
        });
    }
    public static void registerRitual(ResourceLocation recipe, AltarRituals.Handler handler) { AltarRituals.register(id("ritual"), recipe, handler); }

    /** An add-on registers this type and attributes through its own NeoForge deferred register. */
    public static final class Plant extends AnchoredLivingPlant {
        public Plant(EntityType<? extends Plant> type, Level level) { super(type, level); }
        @Override protected ResourceLocation plantType() { return PLANT; }
        @Override protected int growthInterval() { return 72_000; }
        public void anchorAt(BlockPos pos, int direction) { initializeAnchor(pos, direction); }
    }
    public static final class Demon extends GoeticDemon {
        public Demon(EntityType<? extends Demon> type, Level level) { super(type, level, BossEvent.BossBarColor.RED); }
        @Override public int emblem() { return 0; }
        @Override public Optional<ResourceLocation> emblemTexture() { return Optional.of(id("textures/gui/emblem.png")); }
    }
    public static final class Elite extends CorruptionElite<Elite> {
        private final SkillSet<Elite> skills = SkillSet.of();
        public Elite(EntityType<? extends Elite> type, Level level) { super(type, level); }
        @Override protected Elite elite() { return this; }
        @Override public SkillSet<Elite> skills() { return skills; }
        @Override protected void choose(LivingEntity target) {}
        @Override protected void act(LivingEntity target, boolean busy) {}
    }
    private AddonContent() {}
}
