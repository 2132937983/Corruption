package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.CorruptedRemnant;
import java.lang.ref.WeakReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;

/** Unticked visual proxies let vanilla bow/crossbow item properties read the synchronized draw clock. */
final class RemnantVillageWeaponAppearance {
    interface Driven {float drawTicks(float partial);}
    private static final class Clock {
        private final WeakReference<CorruptedRemnant> remnant;
        Clock(CorruptedRemnant remnant){this.remnant=new WeakReference<>(remnant);}
        float draw(float partial){var source=remnant.get();return source==null?-1:source.bowDrawTicks(partial);}
        int used(){return Math.max(0,(int)draw(0)-8);}
    }
    private static final class PillagerProxy extends Pillager implements Driven {
        private final Clock clock;
        PillagerProxy(CorruptedRemnant remnant){super(EntityType.PILLAGER,remnant.level());clock=new Clock(remnant);}
        @Override public float drawTicks(float partial){return clock.draw(partial);}
        @Override public boolean isUsingItem(){return clock!=null&&clock.draw(0)>=8&&!getMainHandItem().isEmpty();}
        @Override public ItemStack getUseItem(){return isUsingItem()?getMainHandItem():ItemStack.EMPTY;}
        @Override public int getTicksUsingItem(){return isUsingItem()?clock.used():0;}
        @Override public int getUseItemRemainingTicks(){return isUsingItem()?getUseItem().getUseDuration(this)-getTicksUsingItem():0;}
        @Override public InteractionHand getUsedItemHand(){return InteractionHand.MAIN_HAND;}
    }
    private static final class IllusionerProxy extends Illusioner implements Driven {
        private final Clock clock;
        IllusionerProxy(CorruptedRemnant remnant){super(EntityType.ILLUSIONER,remnant.level());clock=new Clock(remnant);}
        @Override public float drawTicks(float partial){return clock.draw(partial);}
        @Override public boolean isUsingItem(){return clock!=null&&clock.draw(0)>=8&&!getMainHandItem().isEmpty();}
        @Override public ItemStack getUseItem(){return isUsingItem()?getMainHandItem():ItemStack.EMPTY;}
        @Override public int getTicksUsingItem(){return isUsingItem()?clock.used():0;}
        @Override public int getUseItemRemainingTicks(){return isUsingItem()?getUseItem().getUseDuration(this)-getTicksUsingItem():0;}
        @Override public InteractionHand getUsedItemHand(){return InteractionHand.MAIN_HAND;}
    }
    private static final class PiglinProxy extends net.minecraft.world.entity.monster.piglin.Piglin implements Driven {
        private final Clock clock;
        PiglinProxy(CorruptedRemnant remnant){super(EntityType.PIGLIN,remnant.level());clock=new Clock(remnant);}
        @Override public float drawTicks(float partial){return clock.draw(partial);}
        @Override public boolean isUsingItem(){return clock!=null&&clock.draw(0)>=8&&!getMainHandItem().isEmpty();}
        @Override public ItemStack getUseItem(){return isUsingItem()?getMainHandItem():ItemStack.EMPTY;}
        @Override public int getTicksUsingItem(){return isUsingItem()?clock.used():0;}
        @Override public int getUseItemRemainingTicks(){return isUsingItem()?getUseItem().getUseDuration(this)-getTicksUsingItem():0;}
        @Override public InteractionHand getUsedItemHand(){return InteractionHand.MAIN_HAND;}
    }
    static Mob create(ResourceLocation type,CorruptedRemnant remnant){
        if(type.equals(ResourceLocation.withDefaultNamespace("pillager")))return new PillagerProxy(remnant);
        if(type.equals(ResourceLocation.withDefaultNamespace("illusioner")))return new IllusionerProxy(remnant);
        if(type.equals(ResourceLocation.withDefaultNamespace("piglin")))return new PiglinProxy(remnant);
        return null;
    }
    private RemnantVillageWeaponAppearance(){}
}
