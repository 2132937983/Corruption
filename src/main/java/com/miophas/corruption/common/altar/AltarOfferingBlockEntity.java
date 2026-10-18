package com.miophas.corruption.common.altar;

import com.miophas.corruption.common.block.AltarOfferingBlock;
import com.miophas.corruption.common.entity.CorruptLightning;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.AltarRules;
import com.miophas.corruption.core.altar.AltarState;
import com.miophas.corruption.core.altar.RitualRules;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * An Altar Offering's work: watching over its altar, binding creatures on it, and performing rites.
 *
 * Every {@link AltarRules#CHECK_TICKS} ticks it looks over its altar, the Altar Pillars of a hexagram or an
 * octagram ({@link AltarForm}) turned the way it faces, and changes its state as {@link AltarRules} says, keeping
 * the shape it finds in its block state. While binding,
 * the first creature to step onto it is held at its middle, the only one it holds: any other is pushed off. That
 * one is what rites asking for a creature take. While its altar
 * stands, it looks for a rite in what lies on the pillars, what is bound on it, the ground round it and the
 * players there (see AltarRecipe): one found gathers for its
 * time, the pillars' offerings drifting in; corrupted lightning strikes; and as {@link RitualRules} says, what
 * the rite took is gone, what it brought forth appears on the offering, and the offering stops binding, so
 * that walks free. If what is on the altar changes before then, the rite comes to nothing.
 */
public final class AltarOfferingBlockEntity extends BlockEntity {
    private int sinceCheck;
    /** The rite under way, and how many ticks it has run. */
    private ResourceLocation working;
    private int elapsed;
    /** The creature bound on it; its id is kept with the block, so it is found again once loaded. */
    private Mob bound;
    private UUID boundId;
    private final RecipeManager.CachedCheck<AltarInput, AltarRecipe> recipes = RecipeManager.createCheck(ModRecipes.ALTAR.get());

    public AltarOfferingBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ALTAR_OFFERING.get(), pos, state); }

    public AltarState state() { return getBlockState().getValue(AltarOfferingBlock.ALTAR).state(); }
    public AltarFacing facing() { return AltarOfferingBlock.facing(getBlockState()); }
    /** The shape of its altar, as it last found it standing. */
    public AltarForm form() { return AltarOfferingBlock.form(getBlockState()); }
    public Mob bound() { return bound; }
    public int elapsed() { return elapsed; }

    /**
     * Where a place on its altar lies, on its level: given in blocks east and north of it as though the altar
     * faced north, turned the way it faces.
     */
    public BlockPos place(int east, int north) {
        int[] at = facing().inWorld(east, north);
        return worldPosition.offset(at[0], 0, -at[1]);
    }

    /** Where a pillar of its altar stands, in the shape it last found. */
    private BlockPos pillarPos(int slot) { return place(form().east(slot), form().north(slot)); }

    /** The shape of the altar whose pillars all stand round it, the way it faces: an octagram before a hexagram; or null. */
    public AltarForm standing(Level level) { return AltarForm.standing((east, north) -> level.getBlockState(place(east, north)).is(ModBlocks.ALTAR_PILLAR.get())); }

    /** Whether all the pillars of either shape stand where they should, the way it faces. */
    public boolean formed(Level level) { return standing(level) != null; }

    /** The pillars, slot 1 first; null where one is missing. */
    private List<AltarPillarBlockEntity> pillars(Level level) {
        List<AltarPillarBlockEntity> pillars = new ArrayList<>();
        for (int slot = 0; slot < form().slots(); slot++)
            pillars.add(level.getBlockEntity(pillarPos(slot)) instanceof AltarPillarBlockEntity pillar ? pillar : null);
        return pillars;
    }

    /** Where creatures on it stand: the middle of its top. */
    public Vec3 top() { return new Vec3(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5); }
    /** The space over it, where a creature counts as on it. */
    private AABB over() { return new AABB(worldPosition.getX() - .2, worldPosition.getY() + .4, worldPosition.getZ() - .2, worldPosition.getX() + 1.2, worldPosition.getY() + 2.5, worldPosition.getZ() + 1.2); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarOfferingBlockEntity offering) { offering.tick(level); }

    private void tick(Level level) {
        if (++sinceCheck >= AltarRules.CHECK_TICKS) {
            sinceCheck = 0;
            AltarForm standing = standing(level);
            become(AltarRules.checked(state(), standing != null), standing);
        }
        AltarState state = state();
        if (state.binds()) hold(level); else release();
        if (state.performs()) rite(level, state); else stop();
    }

    /** Used by hand: it looks over its altar at once and, if it stands, starts binding, or stops. Answers what it becomes. */
    public AltarState use(Level level) {
        sinceCheck = 0;
        AltarForm standing = standing(level);
        become(AltarRules.used(state(), standing != null), standing);
        return state();
    }

    /** Takes a state, and the shape found standing, if any; a rite under way in another shape comes to nothing. */
    private void become(AltarState next, AltarForm standing) {
        if (level == null) return;
        BlockState now = getBlockState(), then = now.setValue(AltarOfferingBlock.ALTAR, AltarMode.of(next));
        if (standing != null) then = then.setValue(AltarOfferingBlock.STAR, AltarStar.of(standing));
        if (then == now) return;
        if (standing != null && standing != form()) stop();
        level.setBlock(worldPosition, then, 3);
        if (!next.binds()) release();
        setChanged();
    }

    // Binding

    /** Binds a creature: it is held at the middle of the offering while it binds, and is what rites asking for a creature take. */
    public void bind(Mob mob) {
        bound = mob;
        boundId = mob.getUUID();
        holdStill(mob);
        setChanged();
    }

    private void release() {
        if (bound == null && boundId == null) return;
        bound = null;
        boundId = null;
        setChanged();
    }

    private void hold(Level level) { keep(level.getEntitiesOfClass(Mob.class, over(), Mob::isAlive)); }

    /**
     * Keeps what it binds where it is, taking up a creature on it if it binds nothing yet; it binds one, and
     * pushes any other on it off.
     *
     * @param on the creatures on it
     */
    void keep(List<Mob> on) {
        if (bound != null && (!bound.isAlive() || !bound.getBoundingBox().intersects(over().inflate(.6)))) release();
        if (bound == null) {
            for (Mob mob : on) if (boundId == null || mob.getUUID().equals(boundId)) { bind(mob); break; }
            if (bound == null && boundId != null && !on.isEmpty()) bind(on.get(0));
        }
        if (bound != null) holdStill(bound);
        for (Mob mob : on) if (mob != bound) pushOff(mob);
    }

    /** Pushes a creature off it, away from its middle. */
    private void pushOff(Mob mob) {
        Vec3 away = mob.position().subtract(top()).multiply(1, 0, 1);
        if (away.lengthSqr() < 1e-4) away = new Vec3(Math.cos(mob.getId()), 0, Math.sin(mob.getId()));
        away = away.normalize().scale(.3);
        mob.setDeltaMovement(away.x, Math.max(mob.getDeltaMovement().y, .2), away.z);
        mob.hurtMarked = true;
    }

    private void holdStill(Mob mob) {
        Vec3 top = top();
        if (mob.position().distanceToSqr(top) > .0025) mob.teleportTo(top.x, top.y, top.z);
        mob.setDeltaMovement(0, Math.min(0, mob.getDeltaMovement().y), 0);
        mob.getNavigation().stop();
    }

    // Rites

    /** What is at the altar, as its recipes see it; null if a pillar is missing. */
    private AltarInput input(Level level, AltarState state) {
        List<ItemStack> items = new ArrayList<>();
        for (AltarPillarBlockEntity pillar : pillars(level)) {
            if (pillar == null) return null;
            items.add(pillar.item());
        }
        AltarGround ground = (east, north) -> level.getBlockState(place(east, north));
        return new AltarInput(items, state.binds() && bound != null ? bound.getType() : null, ground, corruptionAt(level), space(level));
    }

    /** What stands round it, as a structure built round its altar sees it, turned the way it faces. */
    public AltarSpace space(Level level) { return AltarStructure.around(level, worldPosition, facing()); }

    /** The altar's whole square, from its ground four blocks up. */
    private AABB square() {
        return new AABB(worldPosition).inflate(AltarLayout.REACH, 0, AltarLayout.REACH).expandTowards(0, 3, 0);
    }

    /** How corrupted each player at the altar is; those only watching do not count. */
    private int[] corruptionAt(Level level) {
        AABB square = square();
        return level.players().stream().filter(player -> player.isAlive() && !player.isSpectator() && square.contains(player.position()))
                .mapToInt(PlayerCorruption::value).toArray();
    }

    private void rite(Level level, AltarState state) {
        AltarInput input = input(level, state);
        RecipeHolder<AltarRecipe> found = input == null || input.isEmpty() ? null : recipes.getRecipeFor(input, level).orElse(null);
        if (found == null || working != null && !found.id().equals(working)) stop();
        if (found == null) return;
        working = found.id();
        AltarRecipe recipe = found.value();
        switch (RitualRules.phase(elapsed, recipe.time())) {
            case GATHERING -> { if (elapsed % 4 == 0) gather(level); }
            case STRIKE -> strike(level);
            case AFTERMATH -> {}
            case COMPLETE -> { complete(level, recipe, input); return; }
        }
        elapsed++;
        setChanged();
    }

    private void stop() {
        if (working == null && elapsed == 0) return;
        working = null;
        elapsed = 0;
        setChanged();
    }

    /** The pillars' offerings drift in towards the middle. */
    private void gather(Level level) {
        if (!(level instanceof ServerLevel server)) return;
        Vec3 top = top().add(0, .4, 0);
        for (int slot = 0; slot < form().slots(); slot++) {
            BlockPos at = pillarPos(slot);
            if (!(level.getBlockEntity(at) instanceof AltarPillarBlockEntity pillar) || pillar.isEmpty()) continue;
            Vec3 from = Vec3.atCenterOf(at).add(0, .9, 0), towards = top.subtract(from).scale(.06);
            server.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), from.x, from.y, from.z, 0, towards.x, towards.y, towards.z, 1);
        }
    }

    /** Corrupted lightning strikes the offering. */
    private void strike(Level level) {
        CorruptLightning bolt = ModEntities.CORRUPT_LIGHTNING.get().create(level);
        if (bolt == null) return;
        Vec3 top = top();
        bolt.moveTo(top.x, top.y, top.z);
        level.addFreshEntity(bolt);
    }

    /** What the rite took is gone, what it brought forth appears, and the offering stops binding. */
    private void complete(Level level, AltarRecipe recipe, AltarInput input) {
        // What it brings forth is made before what it takes is gone: it may keep what was set on one of those things.
        ItemStack made = recipe.assemble(input, level.registryAccess());
        int[] used = recipe.pillarsUsed(input);
        List<AltarPillarBlockEntity> pillars = pillars(level);
        for (int slot : used == null ? new int[0] : used) {
            AltarPillarBlockEntity pillar = pillars.get(slot);
            ItemStack held = pillar.item();
            pillar.set(held.hasCraftingRemainingItem() ? held.getCraftingRemainingItem() : ItemStack.EMPTY);
        }
        if (recipe.offering().isPresent() && bound != null) {
            if (level instanceof ServerLevel server) server.sendParticles(ParticleTypes.SOUL, bound.getX(), bound.getY() + bound.getBbHeight() / 2, bound.getZ(), 16, .3, .4, .3, .02);
            bound.discard();
        }
        Vec3 top = top();
        if (!made.isEmpty()) {
            ItemEntity drop = new ItemEntity(level, top.x, top.y + .1, top.z, made, 0, .15, 0);
            drop.setDefaultPickUpDelay();
            level.addFreshEntity(drop);
        }
        recipe.result().creature().ifPresent(type -> summon(level, type, top));
        level.playSound(null, worldPosition, SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 1, .8F);
        stop();
        become(AltarRules.afterRite(state()), null);
    }

    private static void summon(Level level, EntityType<?> type, Vec3 at) {
        Entity creature = type.create(level);
        if (creature == null) return;
        creature.moveTo(at.x, at.y, at.z, level.random.nextFloat() * 360, 0);
        if (creature instanceof Mob mob && level instanceof ServerLevel server)
            mob.finalizeSpawn(server, server.getCurrentDifficultyAt(BlockPos.containing(at)), MobSpawnType.EVENT, null);
        level.addFreshEntity(creature);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        working = tag.contains("Rite") ? ResourceLocation.tryParse(tag.getString("Rite")) : null;
        elapsed = tag.getInt("Elapsed");
        boundId = tag.hasUUID("Bound") ? tag.getUUID("Bound") : null;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (working != null) tag.putString("Rite", working.toString());
        tag.putInt("Elapsed", elapsed);
        if (boundId != null) tag.putUUID("Bound", boundId);
    }
}
