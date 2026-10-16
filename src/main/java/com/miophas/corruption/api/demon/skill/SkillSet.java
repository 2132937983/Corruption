package com.miophas.corruption.api.demon.skill;

import com.miophas.corruption.core.demon.skill.SkillBook;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * A boss's skills: the modules it carries, each by a name of its own, how long each has to go before it comes round
 * again, and the one running, if any; only one runs at a time. The boss chooses what to begin ({@link #begin}); every
 * tick ({@link #tick}) the set counts the cooldowns down and runs the running skill on, and when it is over starts its
 * cooldown, as long as the boss says ({@code cooldowns}: shorter when it is enraged, say). Saved with the boss, by name.
 *
 * <pre>
 * SkillSet&lt;MyDemon&gt; skills = SkillSet.&lt;MyDemon&gt;of()
 *         .with("gaze", BossSkillApi.gaze(MY_GAZE, 0xC01010, 0xFF6060))
 *         .with("storm", BossSkillApi.strike(MY_STORM, 0x8A2BE2, 0xE0C0FF));
 * </pre>
 */
public final class SkillSet<E extends Mob> {
    private final List<String> names = new ArrayList<>();
    private final List<BossSkill<? super E>> skills = new ArrayList<>();
    private SkillBook book = new SkillBook(0);

    public static <E extends Mob> SkillSet<E> of() { return new SkillSet<>(); }

    /** Adds a skill by a name of its own; its place in the set is the order it was added in. */
    public SkillSet<E> with(String name, BossSkill<? super E> skill) {
        if (names.contains(name)) throw new IllegalArgumentException("Two skills are named " + name);
        if (book.busy()) throw new IllegalStateException("A skill is added while another runs");
        names.add(name);
        skills.add(skill);
        int[] cooling = book.save();
        book = new SkillBook(skills.size());
        book.load(cooling);
        return this;
    }

    public int size() { return skills.size(); }
    public int index(String name) { return names.indexOf(name); }
    public String name(int index) { return names.get(index); }
    public BossSkill<? super E> skill(int index) { return skills.get(index); }

    /** The skill running, by its place, or -1. */
    public int running() { return book.running(); }
    /** Ticks the running skill has run, 0 on the tick it began. */
    public int elapsed() { return book.elapsed(); }
    public boolean busy() { return book.busy(); }
    /** Ticks this skill has to go before it comes round again. */
    public int cooling(int index) { return book.cooling(index); }

    /** Whether this skill has come round and nothing else runs. */
    public boolean ready(int index) { return index >= 0 && index < size() && book.ready(index); }

    /** Whether this skill could be begun against this quarry now: it has come round, and it would serve. */
    public boolean usable(int index, E boss, LivingEntity target) { return ready(index) && skills.get(index).usable(boss, target); }

    /** Begins this skill against this quarry, if it has come round; whether it began. It runs its first tick on the next {@link #tick}. */
    public boolean begin(int index, E boss, LivingEntity target) {
        if (!ready(index) || !book.begin(index)) return false;
        skills.get(index).begin(boss, target);
        return true;
    }

    /**
     * A tick passes: every cooldown is counted down, whatever a skill left behind it is kept up ({@link BossSkill#linger}),
     * and the running skill runs on; once it is over, it ends and starts its cooldown, as long as {@code cooldowns} makes
     * its own (the identity for the cooldown as its rules give it).
     */
    public void tick(E boss, @Nullable LivingEntity target, IntUnaryOperator cooldowns) {
        book.cool();
        for (BossSkill<? super E> skill : skills) skill.linger(boss, target);
        int now = book.advance();
        if (now < 0) return;
        BossSkill<? super E> skill = skills.get(book.running());
        skill.tick(boss, target, now);
        if (skill.timing().over(now + 1)) {
            skill.end(boss, false);
            book.finish(cooldowns.applyAsInt(skill.timing().cooldown()));
        }
    }

    /** Breaks off the running skill, if any: it ends, broken, and starts its cooldown all the same. */
    public void interrupt(E boss, IntUnaryOperator cooldowns) {
        if (!book.busy()) return;
        BossSkill<? super E> skill = skills.get(book.running());
        skill.end(boss, true);
        book.finish(cooldowns.applyAsInt(skill.timing().cooldown()));
    }

    /** The boss is done with its skills for good (it died, or is gone): the running one is broken off, and every one lets go of what it holds. */
    public void cease(E boss) {
        interrupt(boss, cooldown -> cooldown);
        for (BossSkill<? super E> skill : skills) skill.cease(boss);
    }

    /** Brings this skill round at once, however long it had to go: as an enraged boss's might. */
    public void comeRound(int index) { book.comeRound(index); }

    /** Puts off this skill: it comes round no sooner than this many ticks from now. */
    public void holdOff(int index, int ticks) { book.holdOff(index, ticks); }

    /** Each skill's cooldown, by name, to save with the boss. A running skill is not saved: loaded, the boss begins afresh. */
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        for (int index = 0; index < size(); index++) tag.putInt(names.get(index), book.cooling(index));
        return tag;
    }

    /** Each skill's cooldown, from what was saved; a skill the save does not know of has come round. */
    public void load(CompoundTag tag) {
        int[] cooling = new int[size()];
        for (int index = 0; index < size(); index++) cooling[index] = tag.getInt(names.get(index));
        book.load(cooling);
    }
}
