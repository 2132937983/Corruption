package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.CorruptedMobApi;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.EnumMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;

/** Uses dedicated corrupted models and the source renderer for other appearances. */
public final class CorruptedRemnantRenderer extends EntityRenderer<CorruptedRemnant> {
    private record Proxy(int version, LivingEntity entity) {}
    private final Map<CorruptedRemnant, Proxy> proxies = new WeakHashMap<>();
    private final CorruptedZombieRenderer zombieRenderer;
    private final CorruptedSkeletonRenderer skeletonRenderer;
    private final CorruptedSpiderRenderer spiderRenderer;
    private final CorruptedEndermanRenderer endermanRenderer;
    private final CorruptedCreeperRenderer creeperRenderer;
    private final Map<CorruptedLivestockModel.Kind, CorruptedLivestockRenderer> livestockRenderers = new EnumMap<>(CorruptedLivestockModel.Kind.class);
    private final Map<CorruptedVillageModel.Kind, CorruptedVillageRenderer> villageRenderers = new EnumMap<>(CorruptedVillageModel.Kind.class);
    private final Map<CorruptedNetherModel.Kind, CorruptedNetherRenderer> netherRenderers = new EnumMap<>(CorruptedNetherModel.Kind.class);
    private final Map<CorruptedWildlifeModel.Kind, CorruptedWildlifeRenderer> wildlifeRenderers = new EnumMap<>(CorruptedWildlifeModel.Kind.class);

    public CorruptedRemnantRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
        zombieRenderer = new CorruptedZombieRenderer(context);
        skeletonRenderer = new CorruptedSkeletonRenderer(context);
        spiderRenderer = new CorruptedSpiderRenderer(context);
        endermanRenderer = new CorruptedEndermanRenderer(context);
        creeperRenderer = new CorruptedCreeperRenderer(context);
        for (var kind : CorruptedLivestockModel.Kind.values()) livestockRenderers.put(kind, new CorruptedLivestockRenderer(context, kind));
        for (var kind : CorruptedVillageModel.Kind.values()) villageRenderers.put(kind, new CorruptedVillageRenderer(context, kind));
        for (var kind : CorruptedNetherModel.Kind.values()) netherRenderers.put(kind, new CorruptedNetherRenderer(context, kind));
        for (var kind : CorruptedWildlifeModel.Kind.values()) wildlifeRenderers.put(kind, new CorruptedWildlifeRenderer(context, kind));
    }

    public LivingEntity appearanceEntity(CorruptedRemnant remnant) {
        Proxy cached = proxies.get(remnant);
        if (cached != null && cached.version == remnant.appearanceVersion()) return cached.entity;
        var appearance = remnant.appearance();
        LivingEntity proxy = null;
        try {
            if (appearance.hasUUID("PlayerUUID")) {
                GameProfile profile = new GameProfile(appearance.getUUID("PlayerUUID"), appearance.getString("PlayerName"));
                var properties = appearance.getList("PlayerTextures", 10);
                for (int i = 0; i < properties.size(); i++) {
                    var property = properties.getCompound(i);
                    profile.getProperties().put("textures", new Property("textures", property.getString("Value"),
                            property.contains("Signature") ? property.getString("Signature") : null));
                }
                var skin = net.minecraft.client.Minecraft.getInstance().getSkinManager().lookupInsecure(profile);
                proxy = new RemotePlayer((ClientLevel) remnant.level(), profile) {
                    @Override public net.minecraft.client.resources.PlayerSkin getSkin() { return skin.get(); }
                };
            } else {
                ResourceLocation type = ResourceLocation.tryParse(appearance.getString("Type"));
                if (type != null && BuiltInRegistries.ENTITY_TYPE.containsKey(type)) {
                    net.minecraft.world.entity.Entity entity = RemnantVillageWeaponAppearance.create(type, remnant);
                    if (entity == null) entity = type.equals(ResourceLocation.withDefaultNamespace("skeleton"))
                            ? new RemnantSkeletonAppearance(remnant) : type.equals(ResourceLocation.withDefaultNamespace("bogged"))
                            ? new RemnantBoggedAppearance(remnant) : type.equals(ResourceLocation.withDefaultNamespace("evoker"))
                            ? new RemnantEvokerAppearance(remnant) : BuiltInRegistries.ENTITY_TYPE.get(type).create(remnant.level());
                    if (entity instanceof LivingEntity living && !(living instanceof CorruptedRemnant)) proxy = living;
                }
            }
            if (proxy != null && appearance.contains("Entity", 10)) proxy.load(appearance.getCompound("Entity"));
        } catch (RuntimeException failure) {
            LogUtils.getLogger().warn("Could not restore remnant appearance {}", appearance.getString("Type"), failure);
            proxy = null;
        }
        if (proxy == null) proxy = EntityType.ZOMBIE.create(remnant.level());
        // Source appearance intentionally omits UUID. Use the saved, synchronized remnant
        // identity so ornament sockets agree across clients and appearance rebuilds.
        if (hasDedicatedModel(proxy) && proxy.getType() != EntityType.ZOMBIE) proxy.setUUID(remnant.getUUID());
        proxy.setHealth(proxy.getMaxHealth());
        proxy.setInvisible(false);
        proxy.setPose(Pose.STANDING);
        proxy.setCustomName(null);
        proxies.put(remnant, new Proxy(remnant.appearanceVersion(), proxy));
        return proxy;
    }

    @Override
    public void render(CorruptedRemnant remnant, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int light) {
        LivingEntity proxy = appearanceEntity(remnant);
        proxy.setPos(remnant.getX(), remnant.getY(), remnant.getZ());
        proxy.xo = remnant.xo; proxy.yo = remnant.yo; proxy.zo = remnant.zo;
        proxy.setYRot(remnant.getYRot()); proxy.yRotO = remnant.yRotO;
        proxy.setXRot(remnant.getXRot()); proxy.xRotO = remnant.xRotO;
        proxy.yBodyRot = remnant.yBodyRot; proxy.yBodyRotO = remnant.yBodyRotO;
        proxy.yHeadRot = remnant.yHeadRot; proxy.yHeadRotO = remnant.yHeadRotO;
        if (proxy.tickCount != remnant.tickCount) proxy.walkAnimation.update(remnant.walkAnimation.speed(), 1.0F);
        proxy.tickCount = remnant.tickCount;
        proxy.deathTime = remnant.deathTime; proxy.hurtTime = remnant.hurtTime;
        proxy.attackAnim = remnant.attackAnim; proxy.oAttackAnim = remnant.oAttackAnim;
        proxy.swingingArm = remnant.swingingArm;
        proxy.setDeltaMovement(remnant.getDeltaMovement());
        proxy.setOnGround(remnant.onGround());
        RemnantAnimationState.sync(proxy, remnant);
        if (proxy instanceof Mob mob) mob.setAggressive(remnant.isAggressive());
        if (proxy instanceof RemnantEvokerAppearance evoker) evoker.updateSpell();
        if (proxy instanceof net.minecraft.world.entity.monster.warden.Warden warden) {
            if (remnant.castingAbility() == com.miophas.corruption.api.corruption.RemnantAbility.SONIC_BOOM)
                warden.sonicBoomAnimationState.startIfStopped(remnant.tickCount - (int) remnant.abilityCastTicks(0));
            else warden.sonicBoomAnimationState.stop();
        }
        if (proxy instanceof RemnantSkeletonAppearance skeleton) skeleton.updateBow();
        if (proxy instanceof RemnantBoggedAppearance bogged) bogged.updateBow();
        RemnantAttackAnimations.update(proxy, remnant);
        shadowRadius = remnant.getBbWidth() / 2;
        MultiBufferSource appearanceBuffers = hasDedicatedModel(proxy) ? buffers : type -> {
            var layer = proxy instanceof net.minecraft.client.player.AbstractClientPlayer player
                ? CrimsonTextures.INSTANCE.layer(type,player.getSkin().texture(),20) : CrimsonTextures.INSTANCE.layer(type);
            var consumer = buffers.getBuffer(layer);
            return layer == type ? consumer : new CrimsonVertexConsumer(consumer);
        };
        sourceRenderer(proxy).render(proxy, yaw, partialTick, poses, appearanceBuffers, light);
        if (remnant.hasAbility(com.miophas.corruption.api.corruption.RemnantAbility.DRAGON_COMBAT)) remnant.dragonCrystal().ifPresent(pos -> {
            double x=net.minecraft.util.Mth.lerp(partialTick,remnant.xo,remnant.getX()), y=net.minecraft.util.Mth.lerp(partialTick,remnant.yo,remnant.getY()), z=net.minecraft.util.Mth.lerp(partialTick,remnant.zo,remnant.getZ());
            poses.pushPose();poses.translate(0,4,0);
            net.minecraft.client.renderer.entity.EnderDragonRenderer.renderCrystalBeams((float)(pos.getX()+.5-x),(float)(pos.getY()+1.5-y-4),(float)(pos.getZ()+.5-z),partialTick,remnant.tickCount,poses,buffers,light);
            poses.popPose();
        });
        super.render(remnant, yaw, partialTick, poses, buffers, light);
    }

    @SuppressWarnings("unchecked")
    private EntityRenderer<LivingEntity> sourceRenderer(LivingEntity source) {
        if (source.getType() == EntityType.ZOMBIE) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) zombieRenderer;
        if (source.getType() == EntityType.SKELETON) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) skeletonRenderer;
        if (source.getType() == EntityType.SPIDER) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) spiderRenderer;
        if (source.getType() == EntityType.ENDERMAN) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) endermanRenderer;
        if (source.getType() == EntityType.CREEPER) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) creeperRenderer;
        var livestock = CorruptedLivestockModel.Kind.of(source);
        if (livestock != null) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) livestockRenderers.get(livestock);
        var wildlife = CorruptedWildlifeModel.Kind.of(source);
        if (wildlife != null) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) wildlifeRenderers.get(wildlife);
        var village = CorruptedVillageModel.Kind.of(source);
        if (village != null) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) villageRenderers.get(village);
        var nether = CorruptedNetherModel.Kind.of(source);
        if (nether != null) return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) netherRenderers.get(nether);
        return (EntityRenderer<LivingEntity>) (EntityRenderer<?>) entityRenderDispatcher.getRenderer(source);
    }

    @Override
    public ResourceLocation getTextureLocation(CorruptedRemnant remnant) {
        LivingEntity proxy = appearanceEntity(remnant);
        if (proxy.getType() == EntityType.ZOMBIE) return CorruptedZombieRenderer.TEXTURE;
        if (proxy.getType() == EntityType.SKELETON) return CorruptedSkeletonRenderer.TEXTURE;
        if (proxy.getType() == EntityType.SPIDER) return CorruptedSpiderRenderer.TEXTURE;
        if (proxy.getType() == EntityType.ENDERMAN) return CorruptedEndermanRenderer.TEXTURE;
        if (proxy.getType() == EntityType.CREEPER) return CorruptedCreeperRenderer.TEXTURE;
        var livestock = CorruptedLivestockModel.Kind.of(proxy);
        if (livestock != null) return CorruptedLivestockRenderer.texture(livestock);
        var wildlife = CorruptedWildlifeModel.Kind.of(proxy);
        if (wildlife != null) return CorruptedWildlifeRenderer.texture(wildlife);
        var village = CorruptedVillageModel.Kind.of(proxy);
        if (village != null) return CorruptedVillageRenderer.texture(village);
        var nether = CorruptedNetherModel.Kind.of(proxy);
        if (nether != null) return CorruptedNetherRenderer.texture(nether);
        if(proxy instanceof net.minecraft.client.player.AbstractClientPlayer player)
            return CrimsonTextures.INSTANCE.texture(player.getSkin().texture(),20);
        return CrimsonTextures.INSTANCE.texture(sourceRenderer(proxy).getTextureLocation(proxy));
    }

    private static boolean hasDedicatedModel(LivingEntity entity) {
        return CorruptedMobApi.hasDedicatedModel(CorruptedMobKinds.sourceId(entity));
    }
}
