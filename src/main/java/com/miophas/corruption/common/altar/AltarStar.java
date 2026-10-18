package com.miophas.corruption.common.altar;

import com.miophas.corruption.core.altar.AltarForm;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

/**
 * An altar's shape as its offering's block state shows it ("star": hexagram or octagram), and as rites and
 * structures write it ("form"); what each is, is {@link AltarForm}'s. Clients draw the magic circle by it.
 */
public enum AltarStar implements StringRepresentable {
    HEXAGRAM(AltarForm.HEXAGRAM), OCTAGRAM(AltarForm.OCTAGRAM);

    /** A shape by name, as data writes it: "hexagram" or "octagram". */
    public static final Codec<AltarForm> FORM_CODEC = StringRepresentable.fromEnum(AltarStar::values).xmap(AltarStar::form, AltarStar::of);
    public static final StreamCodec<ByteBuf, AltarForm> FORM_STREAM_CODEC = ByteBufCodecs.VAR_INT.map(ordinal -> AltarForm.values()[ordinal], AltarForm::ordinal);

    private final AltarForm form;

    AltarStar(AltarForm form) { this.form = form; }

    public AltarForm form() { return form; }
    public static AltarStar of(AltarForm form) { return values()[form.ordinal()]; }
    @Override public String getSerializedName() { return name().toLowerCase(java.util.Locale.ROOT); }
}
