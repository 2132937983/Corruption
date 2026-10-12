package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CrimsonSkin;

public final class CrimsonSkinApi {
    public static int[] transform(int[] abgr, int width, int height, long seed) {
        return CrimsonSkin.transform(abgr, width, height, seed);
    }
    public static int[] playerProgress(int[] abgr, int width, int height, long seed, int corruption) {
        return CrimsonSkin.playerProgress(abgr,width,height,seed,corruption);
    }
    public static int[] playerTransform(int[] abgr,int width,int height,long seed){return CrimsonSkin.playerTransform(abgr,width,height,seed);}

    private CrimsonSkinApi() {}
}
