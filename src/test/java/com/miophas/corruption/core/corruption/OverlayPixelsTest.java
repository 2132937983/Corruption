package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OverlayPixelsTest {
    @Test void transparentBasePaddingStaysTransparent() { assertEquals(0x00ABCDEF, OverlayPixels.blend(0x00ABCDEF, 0xFFFFFFFF)); }
    @Test void opaqueAndTransparentOverlaysUseTheCorrectChannels() {
        assertEquals(0xFFABCDEF, OverlayPixels.blend(0xFFABCDEF, 0x00123456));
    }
    @Test void flatteningKeepsCutoutAlphaAndDoesNotRequireAnotherRenderPass() {
        assertEquals(0xFF80007F, OverlayPixels.blend(0xFF0000FF, 0x80FF0000));
        assertEquals(0x1180007F, OverlayPixels.blend(0x110000FF, 0x80FF0000));
        assertEquals(0xFF123456, OverlayPixels.blend(0xFFABCDEF, 0xFF123456));
    }
}
