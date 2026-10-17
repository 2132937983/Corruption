package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.common.infection.GroundBlight;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.demon.skill.BlightRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Blighting the ground about the caster ({@link BlightRules}) as the mod carries it out for any creature. As it begins
 * it stands still where it is and a ring of runes marks how far the blight will go. At the end of the wind-up it strikes
 * the ground: everything within reach that is not of its side (players in creative or spectator aside) is hurt with the
 * corruption's hurt and thrown off; a ring of force runs out. Then, if the world gives leave (the mobGriefing rule), the
 * blight runs out over the ground, the top block of each place within reach turning as it comes to it, the nearest
 * first: to the dormant corruption, or taken by the infection, as the rules say. What cannot be turned (a block that
 * holds anything, a liquid, an ore for the dormant corruption, what the infection never takes) is passed over.
 */
public final class BlightSkill implements BossSkill<Mob> {
    /** How far above and below the caster's feet it looks for the top of the ground. */
    private static final int ABOVE = 2, BELOW = 4;

    private final BlightRules rules;
    private final int colour, accent;
    @Nullable private Vec3 origin;
    private final List<BlockPos> ground = new ArrayList<>();
    private final List<LivingEntity> struck = new ArrayList<>();
    private int next, turned;

    public BlightSkill(BlightRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public BlightRules rules() { return rules; }

    /** How many blocks this use has turned, and what it struck. */
    public int turned() { return turned; }
    public List<LivingEntity> struck() { return List.copyOf(struck); }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        origin = boss.position();
        ground.clear();
        struck.clear();
        next = turned = 0;
        boss.getNavigation().stop();
        SkillShows.sound(boss, rules.dormant() ? SoundEvents.WARDEN_DIG : SoundEvents.ROOTED_DIRT_BREAK, 2, .6F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, origin, "rune_ring", rules.radius(), .4, rules.timing().windup() + 6, colour, accent, 0, boss.getYRot(), true);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.getNavigation().stop();
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
        if (origin == null || !(boss.level() instanceof ServerLevel level)) return;
        if (rules.timing().strikes(elapsed)) strike(level, boss);
        while (next < ground.size() && rules.reaching(along(ground.get(next))) <= elapsed) {
            BlockPos at = ground.get(next++);
            if (rules.dormant() ? GroundBlight.dormant(level, at) : GroundBlight.infect(level, at)) {
                turned++;
                level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), at.getX() + .5, at.getY() + 1.1, at.getZ() + .5, 3, .3, .1, .3, .02);
            }
        }
    }

    @Override public void end(Mob boss, boolean broken) {
        origin = null;
        ground.clear();
    }

    /** It strikes the ground: what is near is hurt and thrown off, and the ground the blight will take is found. */
    private void strike(ServerLevel level, Mob boss) {
        Vec3 at = origin;
        double radius = rules.radius();
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(radius, 3, radius), near -> near != boss && near.isAlive()
                && !boss.isAlliedTo(near) && !(near instanceof Player player && (player.isCreative() || player.isSpectator()))
                && rules.reaches(Math.hypot(near.getX() - at.x, near.getZ() - at.z)))) {
            near.hurt(ModDamageTypes.corruption(level, boss), (float) rules.damage());
            Vec3 away = near.position().subtract(at).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? Vec3.directionFromRotation(0, boss.getYRot()) : away.normalize();
            near.push(away.x * rules.knockback(), rules.lift(), away.z * rules.knockback());
            near.hurtMarked = true;
            struck.add(near);
        }
        SkillShows.show(level, at, "shockwave", radius, 1.5, 20, colour, accent, .3, boss.getYRot(), true);
        SkillShows.sound(level, at, SoundEvents.GENERIC_EXPLODE.value(), 1.5F, .6F);
        if (rules.most() <= 0 || !EventHooks.canEntityGrief(level, boss)) return;
        int reach = Mth.ceil(radius), feet = Mth.floor(at.y) - 1;
        for (int east = -reach; east <= reach; east++)
            for (int south = -reach; south <= reach; south++) {
                int x = Mth.floor(at.x) + east, z = Mth.floor(at.z) + south;
                if (!rules.reaches(Math.hypot(x + .5 - at.x, z + .5 - at.z))) continue;
                BlockPos top = GroundBlight.surface(level, x, feet, z, ABOVE, BELOW);
                if (top != null && !GroundBlight.unbreakable(level, top)
                        && (rules.dormant() ? GroundBlight.dormantable(level, top, level.getBlockState(top)) : GroundBlight.infectable(level, top)))
                    ground.add(top);
            }
        ground.sort(Comparator.comparingDouble(this::along));
        while (ground.size() > rules.most()) ground.removeLast();
    }

    /** How far a block is from where the blight runs out from, along the ground. */
    private double along(BlockPos at) { return origin == null ? 0 : Math.hypot(at.getX() + .5 - origin.x, at.getZ() + .5 - origin.z); }
}
