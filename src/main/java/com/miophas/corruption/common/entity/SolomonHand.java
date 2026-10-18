package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.entity.skill.SkillShows;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.SolomonRules;
import com.miophas.corruption.core.demon.skill.HandRules;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/**
 * A giant hand grown out of the ground (HandRules): where it stands is where it will fall. It pushes up out of the earth over a
 * circle of runes, its forearm behind it, rears back, and comes down palm first on the circle: everything there that is not
 * of its caller's side is hurt (the corruption's hurt, or Solomon's, which no robe turns aside) and flung up. Then it sinks back
 * into the ground and is gone. It is not there to be struck at, and is never saved.
 */
public final class SolomonHand extends Entity implements IEntityWithComplexSpawn {
    private HandRules rules = SolomonRules.HANDS;
    @Nullable private UUID caller;
    private int colour = SolomonRules.VIOLET, accent = SolomonRules.GOLD;
    private int age;
    private final List<LivingEntity> struck = new ArrayList<>();

    public SolomonHand(EntityType<? extends SolomonHand> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    /** Raises one here, facing this way, for its caller, as the rules say. */
    public static SolomonHand raise(ServerLevel level, Mob caller, Vec3 at, float yaw, HandRules rules, int colour, int accent) {
        SolomonHand hand = new SolomonHand(ModEntities.SOLOMON_HAND.get(), level);
        hand.caller = caller.getUUID();
        hand.rules = rules;
        hand.colour = colour;
        hand.accent = accent;
        hand.moveTo(at.x, at.y, at.z, yaw, 0);
        level.addFreshEntity(hand);
        SkillShows.show(level, at, "magic_circle", rules.radius(), .4, rules.impact() + 4, colour, accent, 0, yaw, true);
        return hand;
    }

    public HandRules rules() { return rules; }
    public int colour() { return colour; }
    /** How long it has stood, in ticks. */
    public float age(float partial) { return age + partial; }
    /** What its fall caught. */
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    @Override public void tick() {
        super.tick();
        age++;
        if (!(level() instanceof ServerLevel level)) return;
        if (age < rules.rise() && age % 3 == 0) {
            BlockState earth = level.getBlockState(BlockPos.containing(position()).below());
            if (!earth.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, earth), getX(), getY() + .2, getZ(), 12, 1.2, .2, 1.2, .1);
        }
        if (age == rules.impact()) fall(level);
        if (age >= rules.lasts()) discard();
    }

    /** It comes down: what is under it is hurt and flung up, and the ground shakes. */
    private void fall(ServerLevel level) {
        Entity by = caller == null ? null : level.getEntity(caller);
        DamageSource hurt = by instanceof LivingEntity living ? ModDamageTypes.corruption(level, living) : ModDamageTypes.solomon(level, this);
        Vec3 at = position();
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(rules.radius(), 3, rules.radius()), near -> near.isAlive()
                && near != by && !(by != null && (by.isAlliedTo(near) || near.isAlliedTo(by))) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                && rules.catches(Math.hypot(near.getX() - at.x, near.getZ() - at.z), near.getY() - at.y))) {
            near.hurt(hurt, (float) rules.damage());
            near.push(0, rules.launch(), 0);
            near.hurtMarked = true;
            struck.add(near);
        }
        BlockState earth = level.getBlockState(BlockPos.containing(at).below());
        if (!earth.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, earth), at.x, at.y + .2, at.z, 60, rules.radius() / 2, .3, rules.radius() / 2, .25);
        SkillShows.show(level, at, "shockwave", rules.radius() + 1.5, 1.5, 20, colour, accent, .6, getYRot(), true);
        SkillShows.sound(level, at, SoundEvents.ANVIL_LAND, 3, .4F);
        SkillShows.sound(level, at, SoundEvents.GENERIC_EXPLODE.value(), 3, .6F);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 160 * 160; }

    @Override public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(age);
        buffer.writeInt(colour);
    }

    @Override public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        age = buffer.readVarInt();
        colour = buffer.readInt();
    }
}
