package com.miophas.corruption.common.entity;

import com.mojang.logging.LogUtils;
import com.miophas.corruption.api.corruption.RemnantAbility;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.phys.Vec3;

/** A non-ticking, unspawned source proxy calls native ranged methods, never self-destructing AI. */
final class RemnantCombat {
    static final ThreadLocal<CorruptedRemnant> PROJECTILE_OWNER = new ThreadLocal<>();
    private final CorruptedRemnant remnant;
    private Mob proxy;
    private boolean failed;

    RemnantCombat(CorruptedRemnant remnant) {
        this.remnant = remnant;
        var appearance = remnant.appearance();
        var id = ResourceLocation.tryParse(appearance.getString("Type"));
        try {
            if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                var entity = BuiltInRegistries.ENTITY_TYPE.get(id).create(remnant.level());
                if (entity instanceof Mob mob && !(mob instanceof CorruptedRemnant)) {
                    if (appearance.contains("Entity", 10)) mob.load(appearance.getCompound("Entity"));
                    mob.setUUID(remnant.getUUID());
                    mob.setNoAi(true);
                    mob.setHealth(mob.getMaxHealth());
                    proxy = mob;
                }
            }
        } catch (RuntimeException error) { fail(error); }
    }

    int nativeAbilities() {
        if (proxy instanceof net.minecraft.world.entity.animal.AbstractFish || proxy instanceof net.minecraft.world.entity.animal.Dolphin
                || proxy instanceof net.minecraft.world.entity.monster.Guardian || proxy instanceof net.minecraft.world.entity.animal.Squid) return RemnantAbility.SWIMMING.mask();
        if (proxy instanceof net.minecraft.world.entity.animal.axolotl.Axolotl || proxy instanceof Drowned
                || proxy instanceof net.minecraft.world.entity.animal.frog.Frog || proxy instanceof net.minecraft.world.entity.animal.Turtle) return RemnantAbility.AMPHIBIOUS.mask();
        if (proxy instanceof net.minecraft.world.entity.monster.Shulker) return RemnantAbility.SHULKER_COMBAT.mask();
        if (proxy instanceof net.minecraft.world.entity.monster.Evoker) return RemnantAbility.SUMMON_VEX.mask() | RemnantAbility.EVOKER_FANGS.mask();
        if (proxy instanceof net.minecraft.world.entity.monster.warden.Warden) return RemnantAbility.SONIC_BOOM.mask();
        if (proxy instanceof net.minecraft.world.entity.monster.Spider) return RemnantAbility.WALL_CLIMB.mask();
        if (proxy instanceof net.minecraft.world.entity.monster.Vex) return RemnantAbility.FLIGHT.mask() | RemnantAbility.PHASE_WALLS.mask();
        if (proxy instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon) return RemnantAbility.FLIGHT.mask() | RemnantAbility.DRAGON_COMBAT.mask();
        if (proxy instanceof Ghast || proxy instanceof Blaze || proxy instanceof net.minecraft.world.entity.boss.wither.WitherBoss
                || proxy instanceof net.minecraft.world.entity.animal.allay.Allay || proxy instanceof net.minecraft.world.entity.ambient.Bat
                || proxy instanceof net.minecraft.world.entity.animal.Bee || proxy instanceof net.minecraft.world.entity.animal.Parrot
                || proxy instanceof net.minecraft.world.entity.monster.Phantom) return RemnantAbility.FLIGHT.mask();
        return 0;
    }

    boolean ranged() {
        if (failed || proxy == null || proxy instanceof Creeper) return false;
        if (proxy instanceof AbstractSkeleton) return proxy.getMainHandItem().getItem() instanceof BowItem;
        if (proxy instanceof Drowned) return proxy.getMainHandItem().is(Items.TRIDENT);
        if (proxy instanceof CrossbowAttackMob) return proxy.getMainHandItem().getItem() instanceof CrossbowItem;
        return proxy instanceof RangedAttackMob || proxy instanceof Blaze || proxy instanceof Ghast || proxy instanceof net.minecraft.world.entity.monster.breeze.Breeze;
    }
    ItemStack weapon() { return proxy == null ? ItemStack.EMPTY : proxy.getWeaponItem(); }

    private void position(LivingEntity target) {
        proxy.moveTo(remnant.getX(), remnant.getY(), remnant.getZ(), remnant.getYRot(), remnant.getXRot());
        proxy.setCustomName(remnant.getName());
        proxy.setTarget(target);
        proxy.tickCount = remnant.tickCount;
        var health = proxy.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (health != null) health.setBaseValue(remnant.getMaxHealth());
        var damage = proxy.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.setBaseValue(remnant.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE));
        proxy.setHealth(Math.min(remnant.getHealth(), proxy.getMaxHealth()));
    }

    void shoot(LivingEntity target, float distanceFactor) {
        if (!ranged() || remnant.isAlliedTo(target)) return;
        try {
            position(target);
            if (proxy instanceof net.minecraft.world.entity.monster.breeze.Breeze) {
                var projectile = new net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge(net.minecraft.world.entity.EntityType.BREEZE_WIND_CHARGE, remnant.level());
                projectile.setOwner(remnant);projectile.setPos(remnant.getEyePosition());
                var direction=target.getEyePosition().subtract(projectile.position());
                projectile.shoot(direction.x,direction.y,direction.z,.7F,1);
                remnant.level().addFreshEntity(projectile);
                remnant.playSound(net.minecraft.sounds.SoundEvents.BREEZE_SHOOT,1,1);
            } else if (proxy instanceof Blaze || proxy instanceof Ghast) {
                Vec3 direction = new Vec3(target.getX() - remnant.getX(), target.getY(0.5) - remnant.getY(0.5), target.getZ() - remnant.getZ()).normalize();
                if (proxy instanceof Ghast ghast) {
                    var projectile = new LargeFireball(remnant.level(), remnant, direction, ghast.getExplosionPower());
                    projectile.setPos(remnant.getEyePosition().add(direction.scale(0.6)));
                    remnant.level().addFreshEntity(projectile);
                } else {
                    for (int shot = 0; shot < 3; shot++) {
                        var projectile = new SmallFireball(remnant.level(), remnant, direction.add(remnant.getRandom().triangle(0, 0.025), 0, remnant.getRandom().triangle(0, 0.025)).normalize());
                        projectile.setPos(remnant.getEyePosition().add(direction.scale(0.6)));
                        remnant.level().addFreshEntity(projectile);
                    }
                }
            } else if (proxy instanceof Witch) {
                // Native witches heal raiders. Remnants must attack raiders as well.
                var potion = new ThrownPotion(remnant.level(), remnant);
                potion.setItem(PotionContents.createItemStack(Items.SPLASH_POTION, target.isInvertedHealAndHarm() ? Potions.HEALING : Potions.HARMING));
                double dx = target.getX() - remnant.getX(), dz = target.getZ() - remnant.getZ();
                potion.shoot(dx, target.getEyeY() - 1.1 - remnant.getY() + Math.sqrt(dx * dx + dz * dz) * 0.2, dz, 0.75F, 8);
                remnant.level().addFreshEntity(potion);
            } else {
                if (proxy instanceof CrossbowAttackMob) {
                    var crossbow = proxy.getMainHandItem();
                    if (!CrossbowItem.isCharged(crossbow)) crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStack(Items.ARROW)));
                }
                PROJECTILE_OWNER.set(remnant);
                try { ((RangedAttackMob) proxy).performRangedAttack(target, distanceFactor); }
                finally { PROJECTILE_OWNER.remove(); }
            }
            remnant.swing(InteractionHand.MAIN_HAND);
        } catch (RuntimeException error) { fail(error); }
    }
    private void fail(RuntimeException error) {
        failed = true;
        LogUtils.getLogger().warn("Falling back to melee for corrupted remnant appearance {}", remnant.appearance().getString("Type"), error);
    }
}
