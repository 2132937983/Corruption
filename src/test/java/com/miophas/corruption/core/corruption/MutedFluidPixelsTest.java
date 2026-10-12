package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MutedFluidPixelsTest {
    @Test void preservesAlphaAndRemovesAllSaturation() {
        for (int pixel : new int[] {0x7730A0FF, 0xFFEEEE22, 0xFF2244CC}) {
            int result = MutedFluidPixels.transform(pixel);
            assertEquals(pixel >>> 24, result >>> 24);
            assertEquals(result & 255, result >>> 8 & 255);
            assertEquals(result & 255, result >>> 16 & 255);
            assertTrue((result & 255) <= 102);
        }
    }
    @Test void retainsTextureBrightnessDifferencesAndTransparentPadding() {
        assertEquals(0xFF666666, MutedFluidPixels.transform(0xFFFFFFFF));
        assertEquals(0xFF000000, MutedFluidPixels.transform(0xFF000000));
        assertEquals(0x00112233, MutedFluidPixels.transform(0x00112233));
        assertTrue((MutedFluidPixels.transform(0xFFAAAAAA) & 255) > (MutedFluidPixels.transform(0xFF444444) & 255));
    }
}
