package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.common.network.MindControlPayload;
import com.miophas.corruption.core.demon.skill.MindControlRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Taking hold of a mind ({@link MindControlRules}) as the mod carries it out for any boss. Through the wind-up, rings
 * of its power pulse out from the boss, and its quarry hears it whisper; then, if the quarry is a player within reach
 * that the boss can see, their mind is held for as long as the rules say: their client draws their view round to the
 * boss however they turn, and if the rules say so walks them where it wills (MindControlClient). The skill is over
 * once the mind is taken, and the boss is free to use its others on the player; the hold lingers on ({@link #linger})
 * until its time is up or the player gets away, and is let go at once if the taking is broken off or the boss is gone.
 */
public final class MindControlSkill implements RegisteredSkill<MindControlRules> {
    /** How much further than its reach a held player may get before the hold breaks. */
    private static final double SLIPS = 1.5;

    private final MindControlRules rules;
    private final int colour, accent;
    private ServerPlayer held;
    /** Ticks the hold has to go. */
    private int left;

    public MindControlSkill(MindControlRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public MindControlRules rules() { return rules; }

    /** The player whose mind this use holds, if any. */
    public @Nullable ServerPlayer held() { return held; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) {
        return target instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator() && boss.distanceTo(target) <= rules.range() && boss.hasLineOfSight(target);
    }

    @Override public void begin(Mob boss, LivingEntity target) {
        SkillShows.sound(boss, SoundEvents.EVOKER_PREPARE_WOLOLO, 2, .6F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, boss.position(), "resonance", 2.6, boss.getBbHeight() * .6, rules.timing().windup() + 10, colour, accent, 0, boss.getYRot(), false);
        if (target instanceof ServerPlayer player) player.playNotifySound(SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 1.5F, .5F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (target != null) boss.getLookControl().setLookAt(target, 30, 30);
        if (rules.timing().strikes(elapsed) && target instanceof ServerPlayer player && usable(boss, player)) take(boss, player);
    }

    /** Broken off, a mind just taken is let go at once; run its course, its hold goes on. */
    @Override public void end(Mob boss, boolean broken) { if (broken) letGo(); }

    /** The hold, kept up: let go when its time is up, or when the player gets away, dies, or is no longer in play. */
    @Override public void linger(Mob boss, @Nullable LivingEntity target) {
        if (held == null) return;
        if (--left <= 0 || held.isRemoved() || !held.isAlive() || held.isSpectator() || held.isCreative() || held.level() != boss.level()
                || boss.distanceTo(held) > rules.range() * SLIPS) letGo();
    }

    @Override public void cease(Mob boss) { letGo(); }

    private void letGo() {
        if (held != null) MindControlPayload.release(held);
        held = null;
        left = 0;
    }

    private void take(Mob boss, ServerPlayer player) {
        if (held != null && held != player) MindControlPayload.release(held);
        held = player;
        left = rules.holds();
        MindControlPayload.hold(player, boss.getId(), rules.holds(), rules.moves(), rules.grip());
        player.displayClientMessage(Component.translatable(rules.moves() ? "message.corruption.mind_seized" : "message.corruption.mind_held", boss.getDisplayName()), true);
        player.playNotifySound(SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1, .5F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, player.position(), "eye", 1.2, player.getBbHeight() + 1.6, rules.holds(), colour, accent, 0, 0, false);
    }
    @Override public BossSkillRegistry.Type<MindControlRules> type() { return BossSkillApi.MIND_CONTROL; }
    @Override public MindControlRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
