package com.miophas.corruption.core.corruption;

/** Stable naming and source eligibility, without registry or NBT access. */
public final class CorruptionVariants {
    public static String variantId(String namespace, String path) { return "corruption:generated/" + namespace + "/" + path; }
    public static boolean validSource(String namespace, String path, boolean air, boolean liquid) {
        return !air && !liquid && !(namespace.equals("corruption") && path.equals("infected_block"));
    }
    private CorruptionVariants() {}
}
