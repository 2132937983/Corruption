package com.miophas.corruption.common.altar;

import com.miophas.corruption.core.altar.AltarState;
import net.minecraft.util.StringRepresentable;

/** An Altar Offering's state as its block state shows it ("altar": dormant, formed or binding); what each means is {@link AltarState}'s. */
public enum AltarMode implements StringRepresentable {
    DORMANT(AltarState.DORMANT), FORMED(AltarState.FORMED), BINDING(AltarState.BINDING);

    private final AltarState state;

    AltarMode(AltarState state) { this.state = state; }

    public AltarState state() { return state; }
    public static AltarMode of(AltarState state) { return values()[state.ordinal()]; }
    @Override public String getSerializedName() { return name().toLowerCase(java.util.Locale.ROOT); }
}
