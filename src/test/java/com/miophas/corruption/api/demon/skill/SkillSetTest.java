package com.miophas.corruption.api.demon.skill;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.junit.jupiter.api.Test;

/**
 * A boss's set of skills, with skills that only write down what is asked of them: one runs at a time, tick by tick from
 * its beginning to its end, then comes round again after its cooldown (as long as the boss makes it); a broken-off skill
 * ends broken; cooldowns are saved by name. And what add-ons build on leads nowhere into the mod's internals.
 */
class SkillSetTest {
    /** A skill that keeps a diary. */
    private static final class Diary implements BossSkill<Mob> {
        final List<String> lines = new ArrayList<>();
        private final SkillTiming timing;
        boolean serves = true;

        Diary(SkillTiming timing) { this.timing = timing; }

        @Override public SkillTiming timing() { return timing; }
        @Override public boolean usable(Mob boss, LivingEntity target) { return serves; }
        @Override public void begin(Mob boss, LivingEntity target) { lines.add("begin"); }
        @Override public void tick(Mob boss, LivingEntity target, int elapsed) { lines.add("tick " + elapsed); }
        @Override public void end(Mob boss, boolean broken) { lines.add(broken ? "broken" : "end"); }
        @Override public void linger(Mob boss, LivingEntity target) { lingered++; }
        @Override public void cease(Mob boss) { lines.add("ceased"); }
        int lingered;
    }

    @Test void oneSkillRunsAtATimeFromItsBeginningToItsEnd() {
        Diary gaze = new Diary(new SkillTiming(2, 3, 10)), storm = new Diary(new SkillTiming(1, 2, 5));
        SkillSet<Mob> skills = SkillSet.<Mob>of().with("gaze", gaze).with("storm", storm);
        assertEquals(0, skills.index("gaze"));
        assertEquals("storm", skills.name(1));
        assertTrue(skills.begin(0, null, null));
        assertFalse(skills.begin(1, null, null), "one at a time");
        assertTrue(skills.busy());
        for (int tick = 0; tick < 3; tick++) skills.tick(null, null, cooldown -> cooldown * 2);
        assertEquals(List.of("begin", "tick 0", "tick 1", "tick 2", "end"), gaze.lines);
        assertFalse(skills.busy());
        assertEquals(20, skills.cooling(0), "its cooldown, as long as the boss makes it");
        assertFalse(skills.ready(0));
        assertTrue(skills.ready(1));
        storm.serves = false;
        assertFalse(skills.usable(1, null, null), "come round, but it would not serve");
    }

    @Test void aSkillBrokenOffEndsBrokenAndStillComesRoundLater() {
        Diary beam = new Diary(new SkillTiming(5, 20, 30));
        SkillSet<Mob> skills = SkillSet.<Mob>of().with("beam", beam);
        skills.begin(0, null, null);
        skills.tick(null, null, cooldown -> cooldown);
        skills.interrupt(null, cooldown -> cooldown);
        assertEquals(List.of("begin", "tick 0", "broken"), beam.lines);
        assertFalse(skills.busy());
        assertEquals(30, skills.cooling(0));
        skills.holdOff(0, 50);
        assertEquals(50, skills.cooling(0));
        skills.comeRound(0);
        assertTrue(skills.ready(0), "brought round at once");
    }

    /** What a skill leaves behind it is kept up every tick, running or not; and a boss done with its skills ends them all. */
    @Test void everySkillLingersEveryTickAndAllCeaseTogether() {
        Diary hold = new Diary(new SkillTiming(1, 2, 10)), beam = new Diary(new SkillTiming(1, 3, 10));
        SkillSet<Mob> skills = SkillSet.<Mob>of().with("hold", hold).with("beam", beam);
        skills.begin(0, null, null);
        for (int tick = 0; tick < 2; tick++) skills.tick(null, null, cooldown -> cooldown);
        assertFalse(skills.busy());
        assertTrue(skills.begin(1, null, null), "another may begin once the first has run its course");
        skills.tick(null, null, cooldown -> cooldown);
        assertEquals(3, hold.lingered, "the first goes on lingering while the second runs");
        assertEquals(3, beam.lingered);
        skills.cease(null);
        assertEquals(List.of("begin", "tick 0", "broken", "ceased"), beam.lines, "the running one broken off, then ceased");
        assertEquals(List.of("begin", "tick 0", "tick 1", "end", "ceased"), hold.lines, "and the other ceased");
        assertFalse(skills.busy());
    }

    @Test void cooldownsAreSavedByName() {
        SkillSet<Mob> skills = SkillSet.<Mob>of().with("a", new Diary(new SkillTiming(0, 1, 7))).with("b", new Diary(new SkillTiming(0, 1, 9)));
        skills.begin(1, null, null);
        skills.tick(null, null, cooldown -> cooldown);
        CompoundTag saved = skills.save();
        assertEquals(9, saved.getInt("b"));
        SkillSet<Mob> reordered = SkillSet.<Mob>of().with("b", new Diary(new SkillTiming(0, 1, 9))).with("c", new Diary(new SkillTiming(0, 1, 1)));
        reordered.load(saved);
        assertEquals(9, reordered.cooling(0), "by name, wherever it now stands");
        assertEquals(0, reordered.cooling(1), "one the save does not know of has come round");
        assertThrows(IllegalArgumentException.class, () -> reordered.with("b", new Diary(new SkillTiming(0, 1, 1))), "two skills of one name");
    }

    @Test void whatAddOnsBuildOnNeedsNothingInternal() throws Exception {
        Path folder = null;
        for (Path at = Path.of("").toAbsolutePath(); at != null && folder == null; at = at.getParent())
            if (Files.isDirectory(at.resolve("src/main/java/com/miophas/corruption/api/demon"))) folder = at.resolve("src/main/java/com/miophas/corruption/api/demon");
        assertNotNull(folder);
        try (Stream<Path> files = Files.walk(folder)) {
            List<String> inner = files.filter(file -> file.toString().endsWith(".java")).flatMap(file -> {
                try { return Files.readAllLines(file).stream().filter(line -> line.startsWith("import ") && line.contains("com.miophas.corruption.common.")); }
                catch (Exception failure) { throw new RuntimeException(failure); }
            }).toList();
            assertEquals(List.of(), inner, "api/demon reaches into common");
        }
    }
}
