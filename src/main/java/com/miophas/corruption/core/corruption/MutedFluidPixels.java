package com.miophas.corruption.core.corruption;

/** ABGR pixels, zero saturation and 40% brightness, preserving the source alpha. */
public final class MutedFluidPixels {
    public static int transform(int pixel) {
        if (pixel >>> 24 == 0) return pixel;
        int red = pixel & 255, green = pixel >>> 8 & 255, blue = pixel >>> 16 & 255;
        int grey = ((red * 2126 + green * 7152 + blue * 722) / 10000) * 2 / 5;
        return pixel & 0xFF000000 | grey << 16 | grey << 8 | grey;
    }
    private MutedFluidPixels() {}
}
