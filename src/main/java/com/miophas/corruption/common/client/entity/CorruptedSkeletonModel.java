package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.BowItem;
import org.joml.Vector3f;
import org.joml.Quaternionf;

/** Vanilla skeleton proportions with articulated limbs and independent bow aim/release poses. */
public final class CorruptedSkeletonModel extends CorruptedHumanoidModel<Skeleton> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_skeleton"), "main");

    private final ModelPart backSickles, headSickles;

    public CorruptedSkeletonModel(ModelPart root) {
        super(root, "back", "left");
        backSickles = body.getChild("back_sickles");
        headSickles = head.getChild("left_head_sickles");
    }

    private static HumanoidArm bowArm(Skeleton entity) {
        if (entity.getMainHandItem().getItem() instanceof BowItem) return entity.getMainArm();
        if (entity.getOffhandItem().getItem() instanceof BowItem) return entity.getMainArm().getOpposite();
        return null;
    }

    @Override
    protected boolean usesMeleeAnimation(Skeleton entity) { return bowArm(entity) == null; }
    @Override protected float animationAmplitude() { return 2.0F; }
    @Override protected float restingHunch() { return 0.16F; }

    @Override
    public void setupAnim(Skeleton entity, float limbSwing, float stride, float age, float yaw, float pitch) {
        super.setupAnim(entity, limbSwing, stride, age, yaw, pitch);
        placeOrnaments(entity);
        HumanoidArm side = bowArm(entity);
        if (side == null || !entity.isAggressive()) return;
        float mirror = side == HumanoidArm.RIGHT ? 1 : -1;
        float drawTicks = entity instanceof RemnantSkeletonAppearance proxy
                ? proxy.drawTicks(age - entity.tickCount) : entity.isUsingItem() ? entity.getTicksUsingItem() + 8 : 28;
        if (drawTicks < 0 && attackTime <= 0) return;
        float raise = smooth(Mth.clamp(drawTicks / 8, 0, 1));
        float pull = smooth(Mth.clamp((drawTicks - 8) / 20, 0, 1));
        if (attackTime > 0 && drawTicks < 0) { raise = 1 - smooth(Mth.clamp((attackTime - 0.65F) / 0.35F, 0, 1)); pull = 1; }
        float release = Mth.sin(Mth.clamp(attackTime, 0, 1) * Mth.PI);
        Vector3f bowGrip = aimPoint(-2.5F * mirror, 0, -8.0F + release * 0.7F);
        Vector3f drawGrip = aimPoint((Mth.lerp(pull, -1.0F, 3.2F) + release * 2.0F) * mirror,
                -0.5F * pull + release * 1.1F, Mth.lerp(pull, -7.0F, -2.6F) + release * 1.0F);
        blendAim(side == HumanoidArm.RIGHT ? rightArm : leftArm,
                side == HumanoidArm.RIGHT ? rightElbow : leftElbow, bowGrip, raise);
        blendAim(side == HumanoidArm.RIGHT ? leftArm : rightArm,
                side == HumanoidArm.RIGHT ? leftElbow : rightElbow, drawGrip, raise);
    }

    private static float smooth(float value) { return value * value * (3 - 2 * value); }

    private static void blendAim(ModelPart arm, ModelPart elbow, Vector3f grip, float blend) {
        float x = arm.xRot, y = arm.yRot, z = arm.zRot, bend = elbow.xRot;
        aimArm(arm, elbow, grip);
        arm.xRot = Mth.lerp(blend, x, arm.xRot);
        arm.yRot = Mth.lerp(blend, y, arm.yRot);
        arm.zRot = Mth.lerp(blend, z, arm.zRot);
        elbow.xRot = Mth.lerp(blend, bend, elbow.xRot);
    }

    private void placeOrnaments(Skeleton entity) {
        // The renderer supplies the saved remnant UUID. All clients and
        // reloads choose the same safe sockets without syncing data or generating geometry.
        long seed = entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits();
        seed = (seed ^ (seed >>> 30)) * 0xbf58476d1ce4e5b9L;
        seed = (seed ^ (seed >>> 27)) * 0x94d049bb133111ebL;
        seed ^= seed >>> 31;
        int back = (int) seed & 3, temple = (int) (seed >>> 2) & 3;
        backSickles.x += (back - 1.5F) * 0.4F;
        backSickles.y += (back & 1) * 0.35F;
        backSickles.yRot += (back - 1.5F) * 0.025F;
        // Rotate the entire anchored head cluster around the skull, restricted to its rear
        // half. Root position and orientation must rotate together to keep it attached.
        float turn = -temple * Mth.PI / 4;
        Vector3f pivot = new Vector3f(headSickles.x, headSickles.y, headSickles.z).rotateY(turn);
        Vector3f rotation = new Quaternionf().rotationY(turn).mul(new Quaternionf().rotationZYX(
                headSickles.zRot, headSickles.yRot, headSickles.xRot)).getEulerAnglesZYX(new Vector3f());
        headSickles.setPos(pivot.x, pivot.y, pivot.z);
        headSickles.xRot = rotation.x; headSickles.yRot = rotation.y; headSickles.zRot = rotation.z;
    }

    private Vector3f aimPoint(float x, float y, float z) {
        return new Vector3f(x, y, z).rotateX(head.xRot).rotateY(head.yRot).rotateZ(head.zRot)
                .add(head.x, head.y, head.z);
    }

    private static void aimArm(ModelPart arm, ModelPart elbow, Vector3f target) {
        Vector3f offset = new Vector3f(target).sub(arm.x, arm.y, arm.z);
        float upper = 4, lower = (float) Math.sqrt(37);
        float distance = Mth.clamp(offset.length(), lower - upper + 0.01F, upper + lower - 0.01F);
        float bend = -(float) Math.acos(Mth.clamp(
                (distance * distance - upper * upper - lower * lower) / (2 * upper * lower), -1, 1));
        float forward = (float) Math.sqrt(offset.x * offset.x + offset.z * offset.z);
        arm.xRot = (float) Math.atan2(-forward, offset.y)
                - (float) Math.atan2(lower * Math.sin(bend), upper + lower * Math.cos(bend));
        arm.yRot = (float) Math.atan2(-offset.x, -offset.z);
        arm.zRot = 0;
        elbow.xRot = bend + (float) Math.atan2(1, 6);
        elbow.yRot = elbow.zRot = 0;
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poses) {
        super.translateToHand(side, poses);
        // The item layer's standard offset assumes a four-pixel-wide arm. Center the grip
        // on the two-pixel forearm, one pixel in front of its wrist.
        poses.translate(side == HumanoidArm.RIGHT ? 1 / 16.0F : -1 / 16.0F, 0, 1 / 16.0F);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 1.0F));

        PartDefinition left_head_sickles = head.addOrReplaceChild("left_head_sickles", CubeListBuilder.create().texOffs(46, 80).addBox(-0.9F, -1.8F, -1.6F, 2.4F, 2.6F, 3.3F, new CubeDeformation(0.0F))
        .texOffs(61, 80).addBox(0.6F, -2.4F, -1.2F, 2.6F, 2.0F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -6.4F, 1.8F, -0.1222F, 0.0F, 0.1047F));

        PartDefinition left_head_blade_0 = left_head_sickles.addOrReplaceChild("left_head_blade_0", CubeListBuilder.create().texOffs(119, 80).addBox(-0.8F, -0.6F, -0.715F, 1.6F, 1.8F, 1.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.7F, -0.6F, 0.0873F, -0.192F, -0.0698F));

        PartDefinition left_head_blade_0_segment_3_r1 = left_head_blade_0.addOrReplaceChild("left_head_blade_0_segment_3_r1", CubeListBuilder.create().texOffs(23, 90).addBox(-0.0738F, -1.6521F, -0.55F, 0.1476F, 1.8721F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.854F, -3.116F, 0.0F, 0.0F, 0.0F, 1.9832F));

        PartDefinition left_head_blade_0_segment_2_r1 = left_head_blade_0.addOrReplaceChild("left_head_blade_0_segment_2_r1", CubeListBuilder.create().texOffs(16, 90).addBox(-0.2255F, -1.7973F, -0.55F, 0.451F, 2.0173F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.296F, -2.87F, 0.0F, 0.0F, 0.0F, 1.4142F));

        PartDefinition left_head_blade_0_segment_1_r1 = left_head_blade_0.addOrReplaceChild("left_head_blade_0_segment_1_r1", CubeListBuilder.create().texOffs(9, 90).addBox(-0.3895F, -1.9105F, -0.55F, 0.779F, 2.1305F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.984F, -1.804F, 0.0F, 0.0F, 0.0F, 0.8885F));

        PartDefinition left_head_blade_0_segment_0_r1 = left_head_blade_0.addOrReplaceChild("left_head_blade_0_segment_0_r1", CubeListBuilder.create().texOffs(0, 90).addBox(-0.5125F, -2.2749F, -0.55F, 1.025F, 2.4949F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4993F));

        PartDefinition left_head_blade_1 = left_head_sickles.addOrReplaceChild("left_head_blade_1", CubeListBuilder.create().texOffs(73, 90).addBox(-0.8F, -0.6F, -0.715F, 1.6F, 1.8F, 1.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.55F, -1.7F, 0.25F, -0.0349F, 0.0F, 0.0175F));

        PartDefinition left_head_blade_1_segment_3_r1 = left_head_blade_1.addOrReplaceChild("left_head_blade_1_segment_3_r1", CubeListBuilder.create().texOffs(105, 90).addBox(-0.09F, -1.9664F, -0.55F, 0.18F, 2.1864F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7F, -3.8F, 0.0F, 0.0F, 0.0F, 1.9832F));

        PartDefinition left_head_blade_1_segment_2_r1 = left_head_blade_1.addOrReplaceChild("left_head_blade_1_segment_2_r1", CubeListBuilder.create().texOffs(98, 90).addBox(-0.275F, -2.1435F, -0.55F, 0.55F, 2.3635F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, -3.5F, 0.0F, 0.0F, 0.0F, 1.4142F));

        PartDefinition left_head_blade_1_segment_1_r1 = left_head_blade_1.addOrReplaceChild("left_head_blade_1_segment_1_r1", CubeListBuilder.create().texOffs(91, 90).addBox(-0.475F, -2.2816F, -0.55F, 0.95F, 2.5016F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2F, -2.2F, 0.0F, 0.0F, 0.0F, 0.8885F));

        PartDefinition left_head_blade_1_segment_0_r1 = left_head_blade_1.addOrReplaceChild("left_head_blade_1_segment_0_r1", CubeListBuilder.create().texOffs(82, 90).addBox(-0.625F, -2.726F, -0.55F, 1.25F, 2.946F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4993F));

        PartDefinition left_head_blade_2 = left_head_sickles.addOrReplaceChild("left_head_blade_2", CubeListBuilder.create().texOffs(32, 98).addBox(-0.8F, -0.6F, -0.715F, 1.6F, 1.8F, 1.43F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6F, -1.7F, 1.1F, -0.1222F, 0.192F, 0.0873F));

        PartDefinition left_head_blade_2_segment_3_r1 = left_head_blade_2.addOrReplaceChild("left_head_blade_2_segment_3_r1", CubeListBuilder.create().texOffs(64, 98).addBox(-0.081F, -1.7918F, -0.55F, 0.162F, 2.0118F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.23F, -3.42F, 0.0F, 0.0F, 0.0F, 1.9832F));

        PartDefinition left_head_blade_2_segment_2_r1 = left_head_blade_2.addOrReplaceChild("left_head_blade_2_segment_2_r1", CubeListBuilder.create().texOffs(57, 98).addBox(-0.2475F, -1.9512F, -0.55F, 0.495F, 2.1712F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.52F, -3.15F, 0.0F, 0.0F, 0.0F, 1.4142F));

        PartDefinition left_head_blade_2_segment_1_r1 = left_head_blade_2.addOrReplaceChild("left_head_blade_2_segment_1_r1", CubeListBuilder.create().texOffs(50, 98).addBox(-0.4275F, -2.0754F, -0.55F, 0.855F, 2.2954F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.08F, -1.98F, 0.0F, 0.0F, 0.0F, 0.8885F));

        PartDefinition left_head_blade_2_segment_0_r1 = left_head_blade_2.addOrReplaceChild("left_head_blade_2_segment_0_r1", CubeListBuilder.create().texOffs(41, 98).addBox(-0.5625F, -2.4754F, -0.55F, 1.125F, 2.6954F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4993F));

        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition back_sickles = body.addOrReplaceChild("back_sickles", CubeListBuilder.create().texOffs(0, 80).addBox(-3.3F, -1.7F, -2.5F, 4.1F, 2.9F, 5.5F, new CubeDeformation(0.0F))
        .texOffs(23, 80).addBox(-5.7F, -3.0F, -2.1F, 4.1F, 2.4F, 5.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 2.2F, 0.1396F, 1.5708F, -0.1396F));

        PartDefinition back_blade_0 = back_sickles.addOrReplaceChild("back_blade_0", CubeListBuilder.create().texOffs(76, 80).addBox(-0.8F, -0.6F, -1.04F, 1.6F, 1.8F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7F, -2.5F, -1.0F, 0.0873F, 0.2094F, -0.0698F));

        PartDefinition back_blade_0_segment_3_r1 = back_blade_0.addOrReplaceChild("back_blade_0_segment_3_r1", CubeListBuilder.create().texOffs(112, 80).addBox(-0.0984F, -1.9939F, -0.8F, 0.1968F, 2.2139F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.312F, -8.774F, 0.0F, 0.0F, 0.0F, 0.588F));

        PartDefinition back_blade_0_segment_2_r1 = back_blade_0.addOrReplaceChild("back_blade_0_segment_2_r1", CubeListBuilder.create().texOffs(105, 80).addBox(-0.328F, -2.766F, -0.8F, 0.656F, 2.986F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.968F, -6.314F, 0.0F, 0.0F, 0.0F, 0.2606F));

        PartDefinition back_blade_0_segment_1_r1 = back_blade_0.addOrReplaceChild("back_blade_0_segment_1_r1", CubeListBuilder.create().texOffs(96, 80).addBox(-0.5535F, -3.3078F, -0.8F, 1.107F, 3.5278F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.394F, -3.28F, 0.0F, 0.0F, 0.0F, -0.187F));

        PartDefinition back_blade_0_segment_0_r1 = back_blade_0.addOrReplaceChild("back_blade_0_segment_0_r1", CubeListBuilder.create().texOffs(87, 80).addBox(-0.738F, -3.7839F, -0.8F, 1.476F, 4.0039F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4019F));

        PartDefinition back_blade_1 = back_sickles.addOrReplaceChild("back_blade_1", CubeListBuilder.create().texOffs(30, 90).addBox(-0.8F, -0.6F, -1.04F, 1.6F, 1.8F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.25F, -2.5F, 0.65F, -0.0349F, 0.0F, 0.0175F));

        PartDefinition back_blade_1_segment_3_r1 = back_blade_1.addOrReplaceChild("back_blade_1_segment_3_r1", CubeListBuilder.create().texOffs(66, 90).addBox(-0.12F, -2.3833F, -0.8F, 0.24F, 2.6033F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6F, -10.7F, 0.0F, 0.0F, 0.0F, 0.588F));

        PartDefinition back_blade_1_segment_2_r1 = back_blade_1.addOrReplaceChild("back_blade_1_segment_2_r1", CubeListBuilder.create().texOffs(59, 90).addBox(-0.4F, -3.3248F, -0.8F, 0.8F, 3.5448F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -7.7F, 0.0F, 0.0F, 0.0F, 0.2606F));

        PartDefinition back_blade_1_segment_1_r1 = back_blade_1.addOrReplaceChild("back_blade_1_segment_1_r1", CubeListBuilder.create().texOffs(50, 90).addBox(-0.675F, -3.9856F, -0.8F, 1.35F, 4.2056F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7F, -4.0F, 0.0F, 0.0F, 0.0F, -0.187F));

        PartDefinition back_blade_1_segment_0_r1 = back_blade_1.addOrReplaceChild("back_blade_1_segment_0_r1", CubeListBuilder.create().texOffs(41, 90).addBox(-0.9F, -4.5663F, -0.8F, 1.8F, 4.7863F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4019F));

        PartDefinition back_blade_2 = back_sickles.addOrReplaceChild("back_blade_2", CubeListBuilder.create().texOffs(112, 90).addBox(-0.8F, -0.6F, -1.04F, 1.6F, 1.8F, 2.08F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.8F, -2.5F, 2.3F, -0.1222F, -0.2094F, 0.0873F));

        PartDefinition back_blade_2_segment_3_r1 = back_blade_2.addOrReplaceChild("back_blade_2_segment_3_r1", CubeListBuilder.create().texOffs(25, 98).addBox(-0.1056F, -2.1237F, -0.8F, 0.2112F, 2.3437F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.408F, -9.416F, 0.0F, 0.0F, 0.0F, 0.588F));

        PartDefinition back_blade_2_segment_2_r1 = back_blade_2.addOrReplaceChild("back_blade_2_segment_2_r1", CubeListBuilder.create().texOffs(18, 98).addBox(-0.352F, -2.9523F, -0.8F, 0.704F, 3.1723F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.112F, -6.776F, 0.0F, 0.0F, 0.0F, 0.2606F));

        PartDefinition back_blade_2_segment_1_r1 = back_blade_2.addOrReplaceChild("back_blade_2_segment_1_r1", CubeListBuilder.create().texOffs(9, 98).addBox(-0.594F, -3.5338F, -0.8F, 1.188F, 3.7538F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.496F, -3.52F, 0.0F, 0.0F, 0.0F, -0.187F));

        PartDefinition back_blade_2_segment_0_r1 = back_blade_2.addOrReplaceChild("back_blade_2_segment_0_r1", CubeListBuilder.create().texOffs(0, 98).addBox(-0.792F, -4.0447F, -0.8F, 1.584F, 4.2647F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4019F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -0.1047F, 0.0F, 0.0524F));

        PartDefinition right_elbow = right_arm.addOrReplaceChild("right_elbow", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -0.1047F, 0.0F, -0.0524F));

        PartDefinition left_elbow = left_arm.addOrReplaceChild("left_elbow", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.2443F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, -0.0698F, 0.0F, 0.0175F));

        PartDefinition right_knee = right_leg.addOrReplaceChild("right_knee", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.1571F, 0.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, -0.0698F, 0.0F, -0.0175F));

        PartDefinition left_knee = left_leg.addOrReplaceChild("left_knee", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 6.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.1571F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
