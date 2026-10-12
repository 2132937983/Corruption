package com.miophas.corruption.core.corruption;

/** Flatten the translucent overlay into base texels, preserving the source's cutout alpha. */
public final class OverlayPixels {
    public static int blend(int source, int overlay) {
        if (source >>> 24 == 0) return source;
        int alpha = overlay >>> 24;
        int result = source & 0xFF000000;
        for (int shift = 0; shift < 24; shift += 8) {
            int base = source >>> shift & 255, top = overlay >>> shift & 255;
            result |= (base * (255 - alpha) + top * alpha + 127) / 255 << shift;
        }
        return result;
    }
    private OverlayPixels() {}
}
