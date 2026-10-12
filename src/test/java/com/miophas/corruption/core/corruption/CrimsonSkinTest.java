package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class CrimsonSkinTest {
    @Test void legacyAndHighResolutionPlayerSkinsKeepTheHeadUntilLateProgress(){
        for(int[] size:new int[][]{{64,32},{128,128}}){
            int width=size[0],height=size[1],scale=width/64;
            int[] source=new int[width*height];Arrays.fill(source,0xFFB9D1E4);
            int[] midway=CrimsonSkin.playerProgress(source,width,height,63,10);
            for(int y=0;y<16*scale;y++)for(int x=0;x<width;x++)assertEquals(source[y*width+x],midway[y*width+x]);
            assertFalse(Arrays.equals(source,midway));
            int[] late=CrimsonSkin.playerProgress(source,width,height,63,19);
            assertEquals(source[12*scale*width+10*scale],late[12*scale*width+10*scale]);
        }
    }

    @Test
    void tintPreservesAlphaPaddingAndSourceWhileDarkeningOpaqueSkin() {
        int[] pixels = new int[64 * 64];
        Arrays.fill(pixels, 0xFFCCCCCC);
        pixels[0] = 0x00ABCDEF;
        pixels[1] = 0x77CCCCCC;
        int[] original = pixels.clone();
        int[] result = CrimsonSkin.transform(pixels, 64, 64, 123);
        assertArrayEquals(original, pixels);
        assertEquals(pixels[0], result[0]);
        for (int i = 0; i < pixels.length; i++) assertEquals(pixels[i] >>> 24, result[i] >>> 24);
        long tinted = Arrays.stream(result).filter(pixel -> pixel == 0xFF38252D).count();
        assertTrue(tinted > 3800, "Most opaque pixels must use the model's dark grey-purple palette");
    }

    @Test
    void scarsAndBloodAreDeterministicButVaryWithTheTextureSeed() {
        int[] pixels = new int[64 * 64];
        Arrays.fill(pixels, 0xFFCCCCCC);
        int[] one = CrimsonSkin.transform(pixels, 64, 64, 9);
        assertArrayEquals(one, CrimsonSkin.transform(pixels, 64, 64, 9));
        assertFalse(Arrays.equals(one, CrimsonSkin.transform(pixels, 64, 64, 10)));
        assertTrue(Arrays.stream(one).anyMatch(pixel -> pixel == (0xFF000000 | CrimsonSkin.CRACK_COLOR)));
        assertTrue(Arrays.stream(one).anyMatch(pixel -> pixel == (0xFF000000|CrimsonSkin.BLOOD_COLOR) || pixel == (0xFF000000|CrimsonSkin.ACCENT_COLOR)));
    }
    @Test void playerProgressRetainsEyesUntilCompletionAndNeverChangesAlpha() {
        int[] original=new int[4096];Arrays.fill(original,0xFFCCCCCC);original[0]=0;
        assertArrayEquals(original,CrimsonSkin.playerProgress(original,64,64,9,0));
        int[] early=CrimsonSkin.playerProgress(original,64,64,9,6),late=CrimsonSkin.playerProgress(original,64,64,9,19);
        assertNotEquals(original[52*64+20],early[52*64+20]);
        assertEquals(original[12*64+10],late[12*64+10]);
        assertNotEquals(original[9*64+8],late[9*64+8]);
        assertArrayEquals(CrimsonSkin.playerTransform(original,64,64,9),CrimsonSkin.playerProgress(original,64,64,9,20));
        assertEquals(0xFF62459B,CrimsonSkin.playerTransform(original,64,64,9)[13*64+10]);
        for(int i=0;i<original.length;i++)assertEquals(original[i]>>>24,late[i]>>>24);
    }

    @Test
    void fullyTransparentSkinsRemainTransparentAndInvalidSizesAreRejected() {
        int[] transparent = new int[16];
        assertArrayEquals(transparent, CrimsonSkin.transform(transparent, 4, 4, 1));
        assertThrows(IllegalArgumentException.class, () -> CrimsonSkin.transform(transparent, 0, 4, 1));
        assertThrows(IllegalArgumentException.class, () -> CrimsonSkin.transform(transparent, 8, 4, 1));
    }

    @Test void lateProgressEmphasizesOneFacialHalfWhileTheRestOfTheHeadAlsoChanges() {
        for(int[] size:new int[][]{{64,32},{64,64},{128,128}})for(long seed:new long[]{8,9}){
            int w=size[0],h=size[1],scale=w/64;
            int[] source=new int[w*h];Arrays.fill(source,0xFFCCCCCC);
            int[] late=CrimsonSkin.playerProgress(source,w,h,seed,18),complete=CrimsonSkin.playerTransform(source,w,h,seed);
            for(int offset:new int[]{0,32})for(int v=8;v<16;v++)for(int x=0;x<8;x++){
                int index=v*scale*w+(x+8+offset)*scale;
                boolean eye=(v==12||v==13)&&(x==1||x==2||x==5||x==6);
                boolean first=(seed&1)==1?x<4:x>=4;
                assertEquals(first&&!eye?complete[index]:source[index],late[index],"Face must split sideways, with eyes retained");
            }
            for(int[] uv:new int[][]{{25,9},{1,9},{17,9},{9,1},{17,1}}){
                int index=uv[1]*scale*w+uv[0]*scale;
                assertNotEquals(source[index],late[index],"Back, sides, top and underside must also corrupt");
            }
        }
    }
    @Test void headSidesTopAndBackAreAlreadyChangingBeforeTheFinalFacialStage(){
        int[] source=new int[4096];Arrays.fill(source,0xFFCCCCCC);
        int[] progress=CrimsonSkin.playerProgress(source,64,64,9,16);
        for(int offset:new int[]{0,32})for(int[] uv:new int[][]{{25,9},{1,9},{17,9},{9,1},{17,1}}){
            int index=uv[1]*64+uv[0]+offset;
            assertNotEquals(source[index],progress[index]);
        }
        assertEquals(source[12*64+10],progress[12*64+10]);
    }
}
