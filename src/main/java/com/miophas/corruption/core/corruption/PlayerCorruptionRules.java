package com.miophas.corruption.core.corruption;

/** Persistent integer units: two units per heart, no passive recovery. */
public final class PlayerCorruptionRules {
    public static final int MAX=20, HEARTS=10, CURSE_GAIN=1;
    public static int clamp(int value){return Math.clamp(value,0,MAX);}
    public static int increase(int value,int gain){return (int)Math.min(MAX,(long)clamp(value)+Math.max(0,gain));}
    /** Lower by this much, never below none: what a purifier does. */
    public static int decrease(int value,int amount){return (int)Math.max(0,(long)clamp(value)-Math.max(0,amount));}
    public static int heartFill(int value,int index){return Math.clamp(clamp(value)-index*2,0,2);}
    public static boolean completed(int before,int after){return clamp(before)<MAX&&clamp(after)==MAX;}
    private PlayerCorruptionRules(){}
}
