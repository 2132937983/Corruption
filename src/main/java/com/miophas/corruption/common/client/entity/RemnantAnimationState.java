package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.CorruptedRemnant;
import net.minecraft.world.entity.LivingEntity;
import java.util.WeakHashMap;

/** Appearance proxies never tick, so environmental animation flags come from their live remnant. */
public final class RemnantAnimationState {
    private static final class Flags { boolean fluid, riding, crouching; java.lang.ref.WeakReference<CorruptedRemnant> source; }
    private static final WeakHashMap<LivingEntity, Flags> STATES = new WeakHashMap<>();
    public static void sync(LivingEntity appearance, CorruptedRemnant remnant) {
        Flags flags=STATES.computeIfAbsent(appearance,key->new Flags());
        flags.fluid=remnant.isInWater()||remnant.isInLava();flags.riding=remnant.isPassenger();flags.crouching=remnant.isCrouching();
        if(flags.source==null||flags.source.get()!=remnant)flags.source=new java.lang.ref.WeakReference<>(remnant);
    }
    public static float drawTicks(LivingEntity entity,float partial){
        Flags flags=STATES.get(entity);var source=flags==null||flags.source==null?null:flags.source.get();
        return source==null?entity.isUsingItem()?entity.getTicksUsingItem()+8:-1:source.bowDrawTicks(partial);
    }
    public static com.miophas.corruption.api.corruption.RemnantDragonPhase dragonPhase(LivingEntity entity) {
        Flags flags=STATES.get(entity);var source=flags==null||flags.source==null?null:flags.source.get();
        return source==null?com.miophas.corruption.api.corruption.RemnantDragonPhase.HOLDING:source.dragonPhase();
    }
    public static float shulkerPeek(LivingEntity entity,float partial) {
        Flags flags=STATES.get(entity);var source=flags==null||flags.source==null?null:flags.source.get();
        return source==null&&entity instanceof net.minecraft.world.entity.monster.Shulker shulker?shulker.getClientPeekAmount(partial):source==null?0:source.shulkerPeek(partial);
    }
    public static net.minecraft.core.Direction shulkerAttachFace(LivingEntity entity) {
        Flags flags=STATES.get(entity);var source=flags==null||flags.source==null?null:flags.source.get();
        return source==null&&entity instanceof net.minecraft.world.entity.monster.Shulker shulker?shulker.getAttachFace():source==null?net.minecraft.core.Direction.DOWN:source.shulkerAttachFace();
    }
    public static boolean fluid(LivingEntity entity) {
        Flags flags=STATES.get(entity);return flags==null?entity.isInWater()||entity.isInLava():flags.fluid;
    }
    public static int pufferState(LivingEntity entity) {
        if(!(entity instanceof net.minecraft.world.entity.animal.Pufferfish puffer))return 0;
        Flags flags=STATES.get(entity);var source=flags==null||flags.source==null?null:flags.source.get();
        return source==null?puffer.getPuffState():source.isAggressive()?2:0;
    }
    public static boolean riding(LivingEntity entity, boolean modelFlag) {
        Flags flags=STATES.get(entity);return flags==null?modelFlag||entity.isPassenger():flags.riding;
    }
    public static boolean crouching(LivingEntity entity, boolean modelFlag) {
        Flags flags=STATES.get(entity);return flags==null?modelFlag||entity.isCrouching():flags.crouching;
    }
    private RemnantAnimationState() {}
}
