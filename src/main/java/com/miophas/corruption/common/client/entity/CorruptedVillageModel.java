package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptedMobApi;
import com.miophas.corruption.api.corruption.CorruptedMobDefinition;
import com.miophas.corruption.api.corruption.CorruptedMobFamily;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Static village meshes with connected joints, grounded feet and species-specific action poses. */
public final class CorruptedVillageModel extends HierarchicalModel<Mob> implements ArmedModel {
    public enum Kind {
        VILLAGER("villager"), PILLAGER("pillager"), VINDICATOR("vindicator"), EVOKER("evoker"),
        ILLUSIONER("illusioner"), VEX("vex"), RAVAGER("ravager");
        private static final java.util.Map<String, Kind> BY_ID = new java.util.HashMap<>();
        static { for (var kind : values()) BY_ID.put(kind.id, kind); }
        public final String id;
        public final ModelLayerLocation layer;
        private final CorruptedMobDefinition definition;
        Kind(String id) {
            this.id=id;
            definition=CorruptedMobApi.require(CorruptedMobFamily.VILLAGE,id);
            layer=new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"corrupted_"+id),definition.layerVariant());
        }
        public LayerDefinition createLayer() {
            return switch(this) {
                case VILLAGER -> CorruptedVillageGeometry.villager();
                case PILLAGER -> CorruptedVillageGeometry.pillager();
                case VINDICATOR -> CorruptedVillageGeometry.vindicator();
                case EVOKER -> CorruptedVillageGeometry.evoker();
                case ILLUSIONER -> CorruptedVillageGeometry.illusioner();
                case VEX -> CorruptedVillageGeometry.vex();
                case RAVAGER -> CorruptedVillageGeometry.ravager();
            };
        }
        public static Kind of(LivingEntity entity) { return CorruptedMobKinds.of(CorruptedMobFamily.VILLAGE, entity, BY_ID); }
    }
    private final Kind kind;
    private final ModelPart root,body,head,jaw,ornament,neck,robe,leftWing,rightWing;
    private final ModelPart[] parts,fingers;
    private final Joint[] legs,arms;
    private final Vector3f[] hips,footTargets;
    private final float[] legReach;
    private final Map<Mob,Motion> motions=new WeakHashMap<>();
    private static final class Motion {
        float age,air,lastAttack,landed=Float.NEGATIVE_INFINITY;
        int attackIndex;
        boolean ground;
        Motion(Mob entity,float age){this.age=age;ground=entity.onGround();}
        void update(Mob entity,float now,boolean airborne,float attack){
            float elapsed=Mth.clamp(now-age,0,4);
            if(entity.onGround()&&!ground&&air>.1F)landed=now;
            ground=entity.onGround();air+=Mth.clamp((airborne?1:0)-air,-elapsed/3,elapsed/3);
            if(attack>0&&lastAttack<=0)attackIndex++;
            age=now;lastAttack=attack;
        }
    }
    private static final class Joint {
        final ModelPart upper,lower,end;
        final Vector3f a,b,restEnd;
        Joint(ModelPart upper,String lowerName,String endName,ModelPart body){
            this.upper=upper;lower=upper.getChild(lowerName);end=lower.getChild(endName);
            a=new Vector3f(lower.x,lower.y,lower.z);b=new Vector3f(end.x,end.y,end.z);
            restEnd=new Vector3f(body.x+upper.x,body.y+upper.y,body.z+upper.z).add(a).add(b);
        }
        void solve(Vector3f target,Vector3f pole,Quaternionf parent,boolean levelEnd){
            float u=a.length(),l=b.length(),distance=Mth.clamp(target.length(),Math.abs(u-l)+.01F,u+l-.005F);
            Vector3f direction=new Vector3f(target).normalize(),goal=new Vector3f(direction).mul(distance);
            float along=(u*u-l*l+distance*distance)/(2*distance),out=(float)Math.sqrt(Math.max(0,u*u-along*along));
            pole.sub(new Vector3f(direction).mul(pole.dot(direction))).normalize();
            Vector3f knee=new Vector3f(direction).mul(along).add(pole.mul(out));
            Quaternionf upperRotation=new Quaternionf().rotationTo(new Vector3f(a).normalize(),new Vector3f(knee).normalize());
            Vector3f local=new Quaternionf(upperRotation).conjugate().transform(new Vector3f(goal).sub(knee)).normalize();
            Quaternionf lowerRotation=new Quaternionf().rotationTo(new Vector3f(b).normalize(),local);
            rotate(upper,upperRotation);rotate(lower,lowerRotation);
            if(levelEnd)rotate(end,new Quaternionf(parent).mul(upperRotation).mul(lowerRotation).conjugate());
        }
    }
    public CorruptedVillageModel(ModelPart root,Kind kind){
        this.root=root;this.kind=kind;body=root.getChild("body");neck=kind==Kind.RAVAGER?body.getChild("neck"):null;
        head=(neck==null?body:neck).getChild("head");jaw=head.getChild("jaw");
        ornament=(kind==Kind.VILLAGER?head:body).getChild("ornament");
        robe=body.hasChild("robe")?body.getChild("robe"):null;
        leftWing=kind==Kind.VEX?body.getChild("left_wing"):null;rightWing=kind==Kind.VEX?body.getChild("right_wing"):null;
        legs=new Joint[kind==Kind.RAVAGER?4:kind==Kind.VEX?0:2];arms=new Joint[kind==Kind.RAVAGER?0:2];
        hips=new Vector3f[legs.length];footTargets=new Vector3f[legs.length];legReach=new float[legs.length];
        for(int i=0;i<legs.length;i++){
            String name="leg_"+i;legs[i]=new Joint(body.getChild(name),name+"_lower",name+"_foot",body);
            hips[i]=new Vector3f(legs[i].upper.x,legs[i].upper.y,legs[i].upper.z);footTargets[i]=new Vector3f();
            legReach[i]=legs[i].a.length()+legs[i].b.length()-.02F;
        }
        for(int i=0;i<arms.length;i++){String name=i==0?"right_arm":"left_arm";arms[i]=new Joint(body.getChild(name),name+"_lower",name+"_hand",body);}
        fingers=new ModelPart[3];for(int i=0;i<3;i++)fingers[i]=ornament.getChild("finger_"+i);
        parts=root.getAllParts().toArray(ModelPart[]::new);
    }
    @Override public ModelPart root(){return root;}
    @Override public void setupAnim(Mob entity,float swing,float amount,float age,float yaw,float pitch){
        for(var part:parts)part.resetPose();
        boolean ghost=kind==Kind.VEX,beast=kind==Kind.RAVAGER,caster=kind==Kind.EVOKER||kind==Kind.ILLUSIONER;
        boolean spellCasting=caster&&entity instanceof net.minecraft.world.entity.monster.SpellcasterIllager spellcaster&&spellcaster.isCastingSpell();
        boolean fluid=RemnantAnimationState.fluid(entity),seated=RemnantAnimationState.riding(entity,riding),crouched=RemnantAnimationState.crouching(entity,false);
        var motion=motions.computeIfAbsent(entity,key->new Motion(key,age));
        boolean airborne=!entity.onGround()&&!fluid&&!seated&&(Math.abs(entity.getDeltaMovement().y)>.015||motion.air>0);
        float stride=Mth.clamp(amount,0,1),attack=Mth.clamp(attackTime,0,1);motion.update(entity,age,airborne,attack);
        float t=age+Math.floorMod(entity.getUUID().hashCode(),53),breath=Mth.sin(t*.065F),shift=Mth.sin(t*.035F+.6F);
        float idle=(1-smooth(Mth.clamp(stride*2,0,1)))*(attack>0?0:1)*(1-motion.air);
        if(fluid||seated||crouched)idle=0;
        float windup=attack>0&&attack<.28F?smooth(attack/.28F):0;
        float strike=attack>=.28F&&attack<.62F?smooth((attack-.28F)/.34F):attack>=.62F?1-smooth((attack-.62F)/.38F):0;
        if(attack>=.28F&&attack<.62F)windup=1-strike;
        float landing=1-Mth.clamp((age-motion.landed)/7,0,1);
        float side=entity.getMainHandItem().isEmpty()?(motion.attackIndex%2==0?1:-1)
                :entity.getMainArm()==HumanoidArm.RIGHT?1:-1;
        body.y+=idle*(beast?.75F:.55F)+idle*breath*.28F+stride*((beast?1.3F:.75F)+Mth.sin(swing*1.6F)*.14F)
                +landing*1.45F+strike*.95F+(crouched?1:0);
        body.x+=idle*shift*.32F;
        body.xRot+=(beast?.018F:ghost?-.12F:.055F)+idle*breath*.03F-windup*.11F+strike*.15F+stride*.055F
                +motion.air*(entity.getDeltaMovement().y>.02?-.09F:.12F);
        body.zRot+=idle*shift*.055F+Mth.sin(swing*.8F)*stride*.045F;
        body.yRot+=side*(-windup*.12F+strike*.17F);
        body.yScale=1+idle*breath*.012F;
        if(ghost){body.y+=Mth.sin(t*.12F)*.80F;body.xRot+=stride*.38F;}
        if(neck!=null){neck.xRot+=windup*.13F-strike*.25F+idle*breath*.045F;neck.z-=strike*2.2F;}
        head.yRot+=Mth.clamp(yaw*Mth.DEG_TO_RAD,beast?-.55F:-.9F,beast?.55F:.9F)-body.yRot;
        head.xRot+=Mth.clamp(pitch*Mth.DEG_TO_RAD,-.35F,.4F)+idle*Mth.sin(t*.052F)*.075F;
        head.zRot+=idle*shift*.055F+side*strike*.10F;
        jaw.xRot+=.025F+idle*(.065F+.05F*Mth.sin(t*.065F-.8F))+windup*.26F+strike*(beast?.43F:.15F);
        ornament.xRot+=idle*Mth.sin(t*.065F-1.2F)*.085F+landing*.10F;
        ornament.yRot+=(Math.floorMod(entity.getUUID().hashCode(),3)-1)*.10F+idle*shift*.055F;
        for(int i=0;i<3;i++)fingers[i].xRot+=idle*Mth.sin(t*.065F-1.4F+i*.4F)*.05F+windup*.06F;
        if(robe!=null){robe.xRot+=Mth.sin(swing*.8F-.5F)*stride*.10F+motion.air*.08F;robe.zRot-=body.zRot*.65F;}
        if(ghost){
            float flutter=Mth.sin(t*.7F)*(.42F+stride*.24F);
            leftWing.yRot=-.9F-flutter;rightWing.yRot=.9F+flutter;
            leftWing.zRot=-.22F-stride*.24F;rightWing.zRot=.22F+stride*.24F;
            leftWing.xRot=rightWing.xRot=.10F+strike*.30F;
        }
        for(int i=0;i<legs.length;i++){
            var leg=legs[i];boolean front=!beast||i%2==0;
            float phase=swing*(beast?.65F:.85F)+(beast?(i==0||i==3?0:Mth.PI):i*Mth.PI);
            float lift=Math.max(0,Mth.sin(phase))*stride*(beast?2.7F:2.1F);
            Vector3f target=footTargets[i].set(leg.restEnd);target.z+=Mth.cos(phase)*stride*(beast?3.5F:2.5F);
            target.y-=lift+motion.air*(entity.getDeltaMovement().y>.02?(beast?5.2F:3.8F):.80F)+(beast?(front?windup*1.4F:strike*.65F):windup*(i==0?1.1F:0));
            if(fluid){target.y-=1+Mth.sin(t*.16F+i)*.4F;target.z+=Mth.cos(t*.16F+i)*.9F;}
            if(seated)target.y-=2.5F;
        }
        if(!fluid&&!seated&&motion.air<.05F)RemnantAnimationGrounding.lowerBody(body,hips,footTargets,legReach);
        Matrix4f inverse=new Matrix4f().translation(body.x,body.y,body.z).rotateZYX(body.zRot,body.yRot,body.xRot).scale(body.xScale,body.yScale,body.zScale).invert();
        Quaternionf parent=new Quaternionf().rotationZYX(body.zRot,body.yRot,body.xRot);
        for(int i=0;i<legs.length;i++){
            var leg=legs[i];boolean front=!beast||i%2==0;
            Vector3f target=new Vector3f(footTargets[i]);
            inverse.transformPosition(target).sub(leg.upper.x,leg.upper.y,leg.upper.z);
            leg.solve(target,new Vector3f(i<legs.length/2?-.08F:.08F,0,front?1:-1),parent,true);
        }
        boolean crossbow=entity.getMainHandItem().getItem() instanceof CrossbowItem,bow=entity.getMainHandItem().getItem() instanceof BowItem;
        float draw=entity instanceof RemnantVillageWeaponAppearance.Driven driven?driven.drawTicks(age-entity.tickCount)
                :entity.isUsingItem()?entity.getTicksUsingItem()+8:-1;
        boolean ranged=crossbow||bow;
        float raised=draw>=0?smooth(Mth.clamp(draw/8,0,1)):entity.isAggressive()||attack>0?1:0;
        float pull=draw>=0?smooth(Mth.clamp((draw-8)/20,0,1)):windup;
        int main=entity.getMainArm()==HumanoidArm.LEFT?1:0;
        for(int i=0;i<arms.length;i++){
            var arm=arms[i];float sign=i==0?-1:1;
            Vector3f target=new Vector3f(arm.restEnd);
            float unfold=Mth.clamp(stride*2+attack*5+motion.air+ (ghost?1:0),0,1);
            Vector3f folded=new Vector3f(i==0?1.3F:-1.5F,body.getInitialPose().y+1+(i==0?0:1),-4.5F-(i==0?0:1));
            if(!ghost)target.lerp(folded,1-unfold);
            target.z+=Mth.cos(swing*.85F+i*Mth.PI)*stride*3.8F;
            target.y-=motion.air*1.5F+idle*breath*.32F;
            if(ranged){
                float mirror=main==0?1:-1;
                target.set((i==main?(crossbow?-2.0F:-4.5F):(crossbow?1.6F:Mth.lerp(pull,-2,1.5F)))*mirror,
                        body.getInitialPose().y-2.5F+(1-raised)*5,
                        i==main?-9.1F:crossbow?-7.7F:Mth.lerp(pull,-8.2F,-3.6F));
                if(crossbow&&i!=main){target.x-=pull*3.2F*mirror;target.z+=pull*2.3F;}
                target.z+=(1-raised)*2;
                target.z+=strike*.90F;
                float shoulderPlane=body.getInitialPose().y-3;
                target.sub(0,shoulderPlane,0).rotateX(Mth.clamp(pitch*Mth.DEG_TO_RAD,-.35F,.4F)*raised)
                        .rotateY(Mth.clamp(yaw*Mth.DEG_TO_RAD,-.9F,.9F)*raised).add(0,shoulderPlane,0);
            }else if(spellCasting){
                target.set(sign*(7.5F+Mth.sin(t*.16F)*.55F),body.getInitialPose().y-5.0F+Mth.cos(t*.13F)*.45F,-5.5F);
            }else if(caster&&attack>0){
                target.set(sign*(5.5F+windup*3.8F-strike*1.5F),body.getInitialPose().y-3.5F-windup*4.5F+strike*2.8F,-3.5F-strike*5.5F);
            }else{
                boolean active=i==(side>0?0:1);target.y-=(active?windup*7.8F:windup*2.0F);
                target.z+=active?windup*3.5F-strike*(ghost?4.5F:8.0F):-strike*1.8F;
                if(active)target.x-=sign*strike*2.4F;
            }
            if(fluid){target.y-=1;target.z+=Mth.sin(t*.16F+i)*1.2F;}
            if(seated)target.y-=1.5F;
            inverse.transformPosition(target).sub(arm.upper.x,arm.upper.y,arm.upper.z);
            arm.solve(target,new Vector3f(sign,-.12F,ranged?-1:1),parent,false);
            arm.end.xRot-=windup*.06F;
        }
    }
    private static float smooth(float x){return x*x*(3-2*x);}
    private static void rotate(ModelPart part,Quaternionf q){var a=q.getEulerAnglesZYX(new Vector3f());part.xRot=a.x;part.yRot=a.y;part.zRot=a.z;}
    @Override public void translateToHand(HumanoidArm side,PoseStack poses){
        if(arms.length==0)return;
        var arm=arms[side==HumanoidArm.RIGHT?0:1];body.translateAndRotate(poses);arm.upper.translateAndRotate(poses);arm.lower.translateAndRotate(poses);arm.end.translateAndRotate(poses);
        if(kind==Kind.VEX)poses.scale(.55F,.55F,.55F);
        // Cancel the vanilla layer's shoulder-to-wrist offset: this rig already reached the wrist.
        poses.translate((side==HumanoidArm.RIGHT?1:-1)/16.0F,-10/16.0F,2/16.0F);
    }
}
