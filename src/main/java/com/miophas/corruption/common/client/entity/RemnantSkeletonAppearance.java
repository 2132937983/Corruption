package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.CorruptedRemnant;
import java.lang.ref.WeakReference;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.BowItem;

/** Render-only skeleton: the remnant's synchronized clock also drives vanilla bow item properties. */
final class RemnantSkeletonAppearance extends Skeleton {
    private final WeakReference<CorruptedRemnant> remnant;

    RemnantSkeletonAppearance(CorruptedRemnant remnant) {
        super(EntityType.SKELETON, remnant.level());
        this.remnant = new WeakReference<>(remnant);
    }

    float drawTicks(float partialTick) {
        var source = remnant.get();
        return source == null ? -1 : source.bowDrawTicks(partialTick);
    }

    void updateBow() {
        if (drawTicks(0) >= 8) {
            InteractionHand hand = getMainHandItem().getItem() instanceof BowItem
                    ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (getItemInHand(hand).getItem() instanceof BowItem && (!isUsingItem() || getUsedItemHand() != hand)) {
                // Client startUsingItem does not set the using flags. This unspawned visual
                // proxy sets them locally; vanilla's data callback restores the exact held stack.
                setLivingEntityFlag(1, false);
                setLivingEntityFlag(2, hand == InteractionHand.OFF_HAND);
                setLivingEntityFlag(1, true);
            }
        } else if (isUsingItem()) setLivingEntityFlag(1, false);
    }

    @Override public int getUseItemRemainingTicks() {
        return isUsingItem() ? getUseItem().getUseDuration(this) - getTicksUsingItem() : super.getUseItemRemainingTicks();
    }
    @Override public int getTicksUsingItem() {
        return isUsingItem() ? Math.max(0, (int) drawTicks(0) - 8) : 0;
    }
}
