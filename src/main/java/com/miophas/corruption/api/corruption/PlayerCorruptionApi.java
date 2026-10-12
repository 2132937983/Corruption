package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.PlayerCorruptionRules;

/** Progression policy exposed independently of player storage, rendering and effects. */
public final class PlayerCorruptionApi {
    public static int max(){return PlayerCorruptionRules.MAX;}
    public static int curseGain(){return PlayerCorruptionRules.CURSE_GAIN;}
    public static int increase(int value,int gain){return PlayerCorruptionRules.increase(value,gain);}
    public static int heartFill(int value,int index){return PlayerCorruptionRules.heartFill(value,index);}
    public static boolean completed(int before,int after){return PlayerCorruptionRules.completed(before,after);}
    private PlayerCorruptionApi(){}
}
