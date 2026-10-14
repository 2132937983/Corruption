package com.miophas.corruption.api.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

/**
 * Amounts of Corruption Energy in data files: a number of CE (250) or text with any unit ("250CE",
 * "0.1KCE", "20 KCE", "1.5MCE", "2 BCE"). Written back as text, in the unit it reads best in. Use it in your own data formats so they read amounts
 * the same way the mod's do.
 */
public final class CorruptionEnergyCodec {
    private static final Codec<Long> TEXT = Codec.STRING.comapFlatMap(text -> {
        try { return DataResult.success(CorruptionEnergyApi.parse(text)); }
        catch (IllegalArgumentException bad) { return DataResult.error(bad::getMessage); }
    }, CorruptionEnergyApi::format);
    private static final Codec<Long> NUMBER = Codec.LONG.validate(ce -> ce >= 0 ? DataResult.success(ce) : DataResult.error(() -> "Corruption Energy cannot be negative: " + ce));

    public static final Codec<Long> AMOUNT = Codec.withAlternative(TEXT, NUMBER);

    private CorruptionEnergyCodec() {}
}
