package com.miophas.corruption.api.energy;

/**
 * The units Corruption Energy is counted in, each a thousand of the one before, named as English counts
 * (thousand, million, billion and on): CE, KCE, MCE, BCE, TCE, QaCE, QiCE. Amounts are whole CE held in a
 * long, which runs out a little past 9 QiCE, so QiCE is the last. Data files may write any of them, in any
 * case: "1.5MCE", "2 bce", "3QaCE". The rules for reading and writing them are in core.energy.CorruptionEnergy.
 */
public enum CorruptionEnergyUnit {
    /** One Corruption Energy, the least there is. */
    CE("CE", 1L),
    /** A thousand CE. */
    KCE("KCE", 1_000L),
    /** A million CE. */
    MCE("MCE", 1_000_000L),
    /** A billion CE. */
    BCE("BCE", 1_000_000_000L),
    /** A trillion CE. */
    TCE("TCE", 1_000_000_000_000L),
    /** A quadrillion CE. */
    QACE("QaCE", 1_000_000_000_000_000L),
    /** A quintillion CE. */
    QICE("QiCE", 1_000_000_000_000_000_000L);

    private final String symbol;
    private final long ce;

    CorruptionEnergyUnit(String symbol, long ce) { this.symbol = symbol; this.ce = ce; }

    /** How it is written: "KCE", "QaCE". */
    public String symbol() { return symbol; }
    /** How many CE one of it is. */
    public long ce() { return ce; }
}
