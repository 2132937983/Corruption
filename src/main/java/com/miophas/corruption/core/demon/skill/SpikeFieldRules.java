package com.miophas.corruption.core.demon.skill;

import java.util.ArrayList;
import java.util.List;

/**
 * A field of spikes thrown up out of the ground before the boss, as rules. Through the wind-up the places they will
 * come up are marked; at its end they burst up all over a stretch of ground running from {@code near} to {@code far}
 * blocks before the boss (the way it faced as it began), {@code nearWidth} blocks across at its near end and
 * {@code farWidth} at its far, a cluster of them every {@code spacing} blocks; and everything standing in it is hurt for
 * {@code damage} and slowed, {@code slownessTicks} at {@code slownessLevel}.
 */
public record SpikeFieldRules(SkillTiming timing, double minRange, double maxRange, double near, double far, double nearWidth, double farWidth,
                              double spacing, double damage, int slownessTicks, int slownessLevel) {
    public SpikeFieldRules {
        near = Math.max(0, near);
        far = Math.max(near + .5, far);
        nearWidth = Math.max(0, nearWidth);
        farWidth = Math.max(0, farWidth);
        spacing = Math.max(.5, spacing);
        maxRange = Math.max(minRange, maxRange);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** How wide the field is this far before the boss; none outside it. */
    public double widthAt(double ahead) {
        if (ahead < near || ahead > far) return 0;
        return nearWidth + (farWidth - nearWidth) * (ahead - near) / (far - near);
    }

    /** Whether a place this far before the boss and this far to one side of it lies in the field. */
    public boolean covers(double ahead, double aside) { return ahead >= near && ahead <= far && Math.abs(aside) <= widthAt(ahead) / 2; }

    /**
     * Where the clusters of spikes come up, each an offset {ahead, aside}: a row every {@code spacing} from the near end
     * to the far, spread evenly across the field's width there, a cluster every {@code spacing} or so, one at the least.
     */
    public List<double[]> clusters() {
        List<double[]> clusters = new ArrayList<>();
        for (double ahead = near + spacing / 2; ahead <= far + 1e-9; ahead += spacing) {
            double width = widthAt(Math.min(ahead, far));
            int across = Math.max(1, (int) Math.round(width / spacing));
            for (int index = 0; index < across; index++)
                clusters.add(new double[]{ahead, across == 1 ? 0 : -width / 2 + width * (index + .5) / across});
        }
        return clusters;
    }
}
