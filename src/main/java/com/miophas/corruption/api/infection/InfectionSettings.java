package com.miophas.corruption.api.infection;

/** Base per-dimension work budgets and the ceiling for adaptive frontier capacity. */
public record InfectionSettings(int spreadIntervalTicks, int sourcesPerBatch,
        int conversionsPerBatch, double spreadChance, int scanIntervalTicks, int scanBlocksPerBatch,
        double baseCorruptionChance, int maxSpreadMultiplier, boolean enabled) {
    public static final InfectionSettings DEFAULT = new InfectionSettings(100, 64, 8, 0.25, 20, 2048);

    public InfectionSettings(int spreadIntervalTicks, int sourcesPerBatch, int conversionsPerBatch,
            double spreadChance, int scanIntervalTicks, int scanBlocksPerBatch,
            double baseCorruptionChance, int maxSpreadMultiplier) {
        this(spreadIntervalTicks, sourcesPerBatch, conversionsPerBatch, spreadChance, scanIntervalTicks,
                scanBlocksPerBatch, baseCorruptionChance, maxSpreadMultiplier, true);
    }

    public InfectionSettings withEnabled(boolean enabled) {
        return new InfectionSettings(spreadIntervalTicks, sourcesPerBatch, conversionsPerBatch, spreadChance,
                scanIntervalTicks, scanBlocksPerBatch, baseCorruptionChance, maxSpreadMultiplier, enabled);
    }

    public InfectionSettings(int spreadIntervalTicks, int sourcesPerBatch, int conversionsPerBatch,
            double spreadChance, int scanIntervalTicks, int scanBlocksPerBatch) {
        this(spreadIntervalTicks, sourcesPerBatch, conversionsPerBatch, spreadChance, scanIntervalTicks, scanBlocksPerBatch, 0.15);
    }

    public InfectionSettings(int spreadIntervalTicks, int sourcesPerBatch, int conversionsPerBatch,
            double spreadChance, int scanIntervalTicks, int scanBlocksPerBatch, double baseCorruptionChance) {
        this(spreadIntervalTicks, sourcesPerBatch, conversionsPerBatch, spreadChance, scanIntervalTicks,
                scanBlocksPerBatch, baseCorruptionChance, 1);
    }

    public InfectionSettings {
        if (spreadIntervalTicks < 1 || sourcesPerBatch < 1 || conversionsPerBatch < 1
                || !Double.isFinite(spreadChance) || spreadChance < 0 || spreadChance > 1
                || !Double.isFinite(baseCorruptionChance) || baseCorruptionChance < 0 || baseCorruptionChance > 1
                || scanIntervalTicks < 1 || scanBlocksPerBatch < 1 || maxSpreadMultiplier < 1 || maxSpreadMultiplier > 16
                || (long) sourcesPerBatch * maxSpreadMultiplier > Integer.MAX_VALUE
                || (long) conversionsPerBatch * maxSpreadMultiplier > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid infection settings");
        }
    }
}
