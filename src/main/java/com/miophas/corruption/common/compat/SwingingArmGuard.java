package com.miophas.corruption.common.compat;

import com.miophas.corruption.Corruption;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * Keeps a creature's swing whole for whatever else reads it. The game only ever marks a creature as swinging
 * together with the arm it swings ({@code LivingEntity.swing(hand)}); Spore's creatures mark themselves swinging
 * when they strike ({@code UtilityEntity.doHurtTarget} sets {@code swinging}) and name no arm. Goety, seeing a
 * projectile join the world, reads the arm of whoever loosed it if they are swinging, and asks for the item in a
 * hand of null: the server crashes with "Invalid hand null" whenever one of Spore's creatures fires within a few
 * ticks of striking something, a Howitzer lobbing its flesh bomb say.
 *
 * <p>Before anything else hears of a projectile joining the world, an owner that is swinging with no arm is given
 * its main arm, as the game's own swing would have. Nothing else about the creature changes; with neither of
 * those mods present, nothing here ever finds anything to do.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class SwingingArmGuard {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void projectileJoins(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity owner) mend(owner);
    }

    /** Gives a creature that is swinging with no arm its main arm. */
    public static void mend(LivingEntity creature) {
        if (creature.swinging && creature.swingingArm == null) creature.swingingArm = InteractionHand.MAIN_HAND;
    }

    private SwingingArmGuard() {}
}
