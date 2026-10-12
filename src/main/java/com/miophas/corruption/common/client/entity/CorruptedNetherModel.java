package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptedMobApi;
import com.miophas.corruption.api.corruption.CorruptedMobDefinition;
import com.miophas.corruption.api.corruption.CorruptedMobFamily;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Native-derived anatomy; static meshes and per-remnant, species-specific action timelines. */
public final class CorruptedNetherModel extends HierarchicalModel<Mob> implements ArmedModel {
    public enum Kind {
        PIGLIN("piglin"), PIGLIN_BRUTE("piglin_brute"), ZOMBIFIED_PIGLIN("zombified_piglin"), HOGLIN("hoglin"), ZOGLIN("zoglin"), GHAST("ghast"),
        BLAZE("blaze"), MAGMA_CUBE("magma_cube"), STRIDER("strider"), WITHER_SKELETON("wither_skeleton"), WITHER("wither"), ENDERMITE("endermite"),
        ENDER_DRAGON("ender_dragon"), SHULKER("shulker");
        private static final java.util.Map<String, Kind> BY_ID = new java.util.HashMap<>();
        static { for (var kind : values()) BY_ID.put(kind.id, kind); }
        public final String id;
        public final ModelLayerLocation layer;
        private final CorruptedMobDefinition definition;
        Kind(String id){
            this.id=id;
            definition=CorruptedMobApi.require(CorruptedMobFamily.NETHER,id);
            layer=new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"corrupted_"+id),definition.layerVariant());
        }
        public boolean flying(){return definition.flying();}
        public boolean humanoid(){return definition.humanoid();}
        public LayerDefinition createLayer(){return switch(this){
            case PIGLIN -> CorruptedNetherGeometry.piglin();case PIGLIN_BRUTE -> CorruptedNetherGeometry.piglin_brute();
            case ZOMBIFIED_PIGLIN -> CorruptedNetherGeometry.zombified_piglin();case HOGLIN -> CorruptedNetherGeometry.hoglin();
            case ZOGLIN -> CorruptedNetherGeometry.zoglin();case GHAST -> CorruptedNetherGeometry.ghast();case BLAZE -> CorruptedNetherGeometry.blaze();
            case MAGMA_CUBE -> CorruptedNetherGeometry.magma_cube();case STRIDER -> CorruptedNetherGeometry.strider();
            case WITHER_SKELETON -> CorruptedNetherGeometry.wither_skeleton();case WITHER -> CorruptedNetherGeometry.wither();
            case ENDERMITE -> CorruptedNetherGeometry.endermite();case ENDER_DRAGON -> CorruptedNetherGeometry.ender_dragon();case SHULKER -> CorruptedNetherGeometry.shulker();};}
        public static Kind of(LivingEntity entity){return CorruptedMobKinds.of(CorruptedMobFamily.NETHER,entity,BY_ID);}
    }
    private static final class Motion {
        float age,air,lastAttack,landed=Float.NEGATIVE_INFINITY;
        boolean grounded;int attackIndex;
        Motion(Mob entity,float age){this.age=age;grounded=entity.onGround();}
        void update(Mob entity,float now,float attack){
            float elapsed=Mth.clamp(now-age,0,4);
            if(entity.onGround()&&!grounded&&air>.1F)landed=now;
            grounded=entity.onGround();boolean airborne=!grounded&&!RemnantAnimationState.fluid(entity);
            air+=Mth.clamp((airborne?1:0)-air,-elapsed/3,elapsed/3);
            if(attack>0&&lastAttack<=0)attackIndex++;lastAttack=attack;age=now;
        }
    }
    private final class Limb {
        final String name;final ModelPart upper,lower,end;
        final Vector3f a,b,rest;final float reach,kneeClearance;
        Limb(String name,boolean arm){
            this.name=name;upper=bones.get(name);lower=bones.get(name+"_lower");end=bones.get(name+(arm?"_hand":"_foot"));
            a=new Vector3f(lower.x,lower.y,lower.z);b=new Vector3f(end.x,end.y,end.z);
            rest=new Vector3f(body.x+upper.x,body.y+upper.y,body.z+upper.z).add(a).add(b);reach=a.length()+b.length()-.02F;
            float[] radius={0};lower.visit(new PoseStack(),(pose,path,index,c)->{
                if(path.isEmpty())radius[0]=Math.max(radius[0],(float)Math.hypot(Math.max(Math.abs(c.minX),Math.abs(c.maxX)),Math.max(Math.abs(c.minZ),Math.abs(c.maxZ))));
            });kneeClearance=radius[0]+.2F;
        }
        void solve(Vector3f target,boolean arm,Quaternionf parent){
            float u=a.length(),l=b.length(),distance=Mth.clamp(target.length(),Math.abs(u-l)+.01F,u+l-.005F);
            Vector3f direction=new Vector3f(target).normalize(),goal=new Vector3f(direction).mul(distance);
            float along=(u*u-l*l+distance*distance)/(2*distance),out=(float)Math.sqrt(Math.max(0,u*u-along*along));
            Vector3f pole=new Vector3f(arm?(name.startsWith("right")?-.5F:.5F):.08F,arm?-.2F:-1.5F,arm?1:name.contains("hind")?1:-1);
            pole.sub(new Vector3f(direction).mul(pole.dot(direction))).normalize();
            if(!arm){
                // Thick knees must stay above the floor even when a large torso turn folds the leg.
                Vector3f up=new Quaternionf(parent).conjugate().transform(new Vector3f(0,-1,0));
                up.sub(new Vector3f(direction).mul(up.dot(direction))).normalize();
                Vector3f origin=new Vector3f(upper.x,upper.y,upper.z).add(new Vector3f(direction).mul(along));
                float limit=24-kneeClearance;
                if(body.y+new Quaternionf(parent).transform(new Vector3f(origin).add(new Vector3f(pole).mul(out))).y>limit){
                    Vector3f wanted=new Vector3f(pole);float low=0,high=1;
                    for(int step=0;step<8;step++){
                        float blend=(low+high)*.5F;Vector3f test=new Vector3f(wanted).lerp(up,blend).normalize();
                        float y=body.y+new Quaternionf(parent).transform(new Vector3f(origin).add(test.mul(out))).y;
                        if(y>limit)low=blend;else high=blend;
                    }
                    pole.set(wanted).lerp(up,high).normalize();
                }
            }
            Vector3f knee=new Vector3f(direction).mul(along).add(pole.mul(out));
            Quaternionf uq=new Quaternionf().rotationTo(new Vector3f(a).normalize(),new Vector3f(knee).normalize());
            Vector3f local=new Quaternionf(uq).conjugate().transform(new Vector3f(goal).sub(knee)).normalize();
            Quaternionf lq=new Quaternionf().rotationTo(new Vector3f(b).normalize(),local);
            rotate(upper,uq);rotate(lower,lq);if(!arm)rotate(end,new Quaternionf(parent).mul(uq).mul(lq).conjugate());
        }
    }
    private final Kind kind;
    private final ModelPart root,body;
    private final Map<String,ModelPart> bones;
    private final ModelPart[] all;
    private final List<Limb> legs=new ArrayList<>(),arms=new ArrayList<>();
    private final Vector3f[] hips,targets;
    private final float[] reaches;
    private final Map<Mob,Motion> motions=new WeakHashMap<>();
    public CorruptedNetherModel(ModelPart root,Kind kind){
        this.root=root;this.kind=kind;bones=CorruptedNetherGeometry.parts(root,kind.id);body=bones.get("corruption_root");all=root.getAllParts().toArray(ModelPart[]::new);
        for(String name:List.of("right_leg","left_leg","right_front_leg","left_front_leg","right_hind_leg","left_hind_leg"))
            if(bones.containsKey(name+"_lower"))legs.add(new Limb(name,false));
        if(kind.humanoid()){arms.add(new Limb("right_arm",true));arms.add(new Limb("left_arm",true));}
        hips=new Vector3f[legs.size()];targets=new Vector3f[legs.size()];reaches=new float[legs.size()];
        for(int i=0;i<legs.size();i++){var leg=legs.get(i);hips[i]=new Vector3f(leg.upper.x,leg.upper.y,leg.upper.z);targets[i]=new Vector3f();reaches[i]=leg.reach;}
    }
    @Override public ModelPart root(){return root;}
    private void turn(String name,float x,float y,float z){var part=bones.get(name);if(part!=null){part.xRot+=x;part.yRot+=y;part.zRot+=z;}}
    private static float smooth(float f){f=Mth.clamp(f,0,1);return f*f*(3-2*f);}
    @Override public void setupAnim(Mob entity,float swing,float amount,float age,float yaw,float pitch){
        for(var p:all)p.resetPose();var motion=motions.computeIfAbsent(entity,e->new Motion(e,age));motion.update(entity,age,attackTime);
        float t=age+Math.floorMod(entity.getUUID().hashCode(),251),breath=Mth.sin(t*.075F),shift=Mth.sin(t*.042F),stride=Mth.clamp(amount*1.65F,0,1);
        float idle=(1-stride*.75F)*(1-Mth.clamp(attackTime*5,0,1)),wind=attackTime<.3F?smooth(attackTime/.3F):attackTime<.65F?1-smooth((attackTime-.3F)/.35F):0;
        float hit=attackTime>=.3F&&attackTime<.65F?smooth((attackTime-.3F)/.35F):attackTime>=.65F?1-smooth((attackTime-.65F)/.35F):0;
        float land=1-Mth.clamp((age-motion.landed)/9,0,1),side=(motion.attackIndex&1)==0?1:-1;
        boolean rise=entity.getDeltaMovement().y>.02,dragon=kind==Kind.ENDER_DRAGON;
        float scale=dragon?3.5F:kind==Kind.ENDERMITE?.25F:1;
        body.y+=scale*(idle*breath*.65F+stride*(.5F+Mth.sin(swing*1.5F)*.15F)+land*1.8F+hit*.65F);
        body.xRot+=idle*breath*.065F+stride*.10F-wind*.18F+hit*.25F+motion.air*(rise?-.16F:.18F);
        body.zRot+=idle*shift*.085F+Mth.sin(swing*.75F)*stride*.075F+side*(wind*.10F-hit*.13F);
        body.yRot+=side*(-wind*.18F+hit*.28F);
        if(kind==Kind.HOGLIN||kind==Kind.ZOGLIN){body.xRot*=.6F;body.yRot*=.6F;body.zRot*=.6F;}
        turn("head",idle*Mth.sin(t*.054F)*.10F+Mth.clamp(pitch*Mth.DEG_TO_RAD,-.4F,.5F)-wind*.18F+hit*.22F,
                idle*shift*.10F+Mth.clamp(yaw*Mth.DEG_TO_RAD,-.8F,.8F),idle*shift*.065F);
        for(var entry:bones.entrySet()){
            String name=entry.getKey();var p=entry.getValue();
            if(!name.equals("ghast_jaw")&&(name.endsWith("_jaw")||name.equals("jaw")))p.xRot+=idle*(.10F+Mth.sin(t*.075F-.5F)*.09F)+wind*.45F+hit*.25F;
            if(name.contains("_finger_")&&!name.endsWith("_r1"))p.xRot+=idle*Mth.sin(t*.075F-name.hashCode()%4*.3F)*.10F+wind*.10F+land*.15F;
            if(name.endsWith("_claw"))p.zRot+=idle*shift*.045F+land*.10F;
            if(name.endsWith("_ear"))p.zRot+=Mth.sin(t*.14F+(name.startsWith("right")?0:1.5F))*(.16F+stride*.15F)+wind*.14F;
            if(name.contains("bristle"))p.zRot+=Mth.sin(t*.13F+name.hashCode()%5)*(.13F+stride*.2F)+hit*.16F;
        }
        if(kind==Kind.HOGLIN||kind==Kind.ZOGLIN)turn("head",wind*.43F-hit*.75F,side*hit*.15F,0);
        if(kind==Kind.STRIDER)turn("body",idle*breath*.07F-hit*.22F,yaw*Mth.DEG_TO_RAD*.25F,idle*shift*.08F);
        poseLimbs(entity,swing,stride,t,idle,breath,wind,hit,motion,side,rise);
        if(kind==Kind.GHAST){
            var jaw=bones.get("ghast_jaw");
            // Only the independent jaw opens widely; the floating body remains nearly upright.
            body.xRot=idle*breath*.025F-wind*.04F+hit*.055F+motion.air*(rise?-.025F:.035F);
            body.zRot=idle*shift*.03F;body.yRot=side*(-wind*.04F+hit*.06F);
            float jawAngle=(67.5F+12.5F*Mth.sin(t*.095F-.65F))*Mth.DEG_TO_RAD;
            jaw.xRot=Mth.clamp(jawAngle+wind*.08F-hit*.06F,55*Mth.DEG_TO_RAD,80*Mth.DEG_TO_RAD);
            jaw.zRot=idle*Mth.sin(t*.065F)*.055F; jaw.y+=idle*Mth.sin(t*.095F-.65F)*.22F;
            var tongue=bones.get("tongue_limb");
            if(tongue!=null){
                tongue.xRot+=67.5F*Mth.DEG_TO_RAD-jaw.xRot+idle*Mth.sin(t*.10F)*.14F-wind*.28F+hit*.48F;
                tongue.yRot+=idle*Mth.sin(t*.065F)*.18F; tongue.zRot+=idle*shift*.08F;
                turn("tongue_limb_middle",idle*Mth.sin(t*.10F-.8F)*.23F+wind*.35F-hit*.42F,0,idle*Mth.sin(t*.075F-.6F)*.13F);
                turn("tongue_limb_tip",idle*Mth.sin(t*.10F-1.6F)*.28F+wind*.28F-hit*.40F,0,idle*Mth.sin(t*.075F-1.2F)*.15F);
                turn("tongue_terminal_claw",idle*Mth.sin(t*.10F-2)*.20F+wind*.30F-hit*.50F,0,idle*shift*.12F);
            }
            for(int i=0;i<9;i++){
            float phase=t*.12F+i*.71F;turn("tentacle"+i,75*Mth.DEG_TO_RAD-jaw.xRot+.26F+Mth.sin(phase)*(.28F+stride*.18F)-wind*.36F+hit*.45F,0,Mth.cos(phase*.7F)*.17F);
            turn("tentacle"+i+"_lower",Mth.sin(phase-1)*.40F+wind*.35F-hit*.65F,0,Mth.cos(phase-1)*.18F);
        }
        }
        if(kind==Kind.SHULKER){
            body.resetPose();
            float peek=RemnantAnimationState.shulkerPeek(entity,age-entity.tickCount),open=com.miophas.corruption.api.corruption.RemnantShulkerApi.physicalPeek(peek);
            // The mimic shell stays hinged open even while its defensive peek state is closed.
            var lid=bones.get("lid");
            lid.xRot=Mth.clamp(lid.xRot-open*.23F-breath*.05F-wind*.12F+hit*.09F+land*.04F,
                -70*Mth.DEG_TO_RAD,-28*Mth.DEG_TO_RAD);
            var head=bones.get("head");head.yRot=(entity.yHeadRot-180)*Mth.DEG_TO_RAD+open*shift*.10F;head.y+=open*(breath*.35F-wind*.6F+hit*.9F);
            head.visible=true;
            turn("head_jaw",wind*.25F+hit*.35F,0,0);
            for(var entry:bones.entrySet())if(entry.getKey().startsWith("maw_")&&entry.getKey().endsWith("_claw")){
                float phase=t*.10F+entry.getKey().hashCode()*.013F;
                turn(entry.getKey(),Mth.sin(phase)*.065F-wind*.10F+hit*.16F,0,Mth.cos(phase*.8F)*.035F);
            }
            // Keep the rear hinge attached while expressing airborne and landing states through rotation.
            if(motion.air>.05F)lid.xRot+=motion.air*(rise?-.04F:.04F);
            lid.xRot=Mth.clamp(lid.xRot,-70*Mth.DEG_TO_RAD,-28*Mth.DEG_TO_RAD);
        }
        if(kind==Kind.BLAZE)for(int i=0;i<12;i++){
            var p=bones.get("part"+i);int ring=i/4;float angle=t*(ring==1?.065F:-.08F)+i*Mth.HALF_PI,rad=(9-ring*2)*(1+wind*.3F-hit*.28F);
            p.x=Mth.cos(angle)*rad;p.z=Mth.sin(angle)*rad;p.y+=Mth.sin(t*.18F+i)*1.5F-motion.air*(rise?1:-1)+wind*2-hit*3;
            p.zRot+=Mth.cos(angle)*(.12F+wind*.22F);p.xRot+=Mth.sin(angle)*(.12F+hit*.28F);
        }
        if(kind==Kind.MAGMA_CUBE){
            float squash=idle*breath*.055F+land*.28F+wind*.15F-hit*.12F-motion.air*(rise?.25F:-.08F);
            for(int i=0;i<8;i++){var p=bones.get("cube"+i);p.y+=(i-7)*squash*1.8F;p.xScale=p.zScale=1+squash*.65F;p.yRot+=Mth.sin(t*.1F+i*.35F)*.04F+side*wind*(7-i)*.025F;}
        }
        if(kind==Kind.ENDERMITE){
            for(int i=0;i<4;i++){float wave=Mth.sin(t*(stride>.1F?.42F:.18F)+i*.9F);turn("segment"+i,wind*.12F-hit*.2F,wave*(.10F+stride*.13F),wave*.08F);}
        }
        if(kind==Kind.WITHER){
            turn("ribcage",idle*breath*.16F-wind*.23F+hit*.28F,0,idle*shift*.08F);turn("tail",idle*Mth.sin(t*.075F-.6F)*.26F+stride*.22F,shift*.12F,0);
            turn("center_head",pitch*Mth.DEG_TO_RAD-wind*.2F+hit*.3F,yaw*Mth.DEG_TO_RAD,shift*.09F);
            turn("right_head",Mth.sin(t*.09F)*.17F-wind*.24F+hit*.30F,yaw*Mth.DEG_TO_RAD*.5F+Mth.sin(t*.063F)*.32F,-.08F);
            turn("left_head",Mth.sin(t*.09F+1.5F)*.17F-wind*.20F+hit*.28F,yaw*Mth.DEG_TO_RAD*.5F+Mth.sin(t*.063F+2)*.32F,.08F);
        }
        if(dragon){
            var phase=RemnantAnimationState.dragonPhase(entity);
            boolean sitting=phase.sitting();
            poseDragon(t,stride,wind,hit,sitting?0:motion.air,rise,land,yaw,pitch);
            if(sitting){body.xRot=breath*.035F;body.zRot=shift*.035F;
                turn("jaw",phase==com.miophas.corruption.api.corruption.RemnantDragonPhase.BREATH?.75F:phase==com.miophas.corruption.api.corruption.RemnantDragonPhase.ROARING?.95F:.1F,0,0);
                for(int i=0;i<5;i++)turn("neck_"+i,.045F+Mth.sin(t*.11F-i*.3F)*.025F,0,0);
            }
            if(phase==com.miophas.corruption.api.corruption.RemnantDragonPhase.CHARGE){body.xRot-=.25F;turn("jaw",.45F,0,0);}
            if(phase==com.miophas.corruption.api.corruption.RemnantDragonPhase.TAKEOFF)body.xRot-=.20F;
        }
    }
    private void poseLimbs(Mob entity,float swing,float stride,float t,float idle,float breath,float wind,float hit,Motion motion,float side,boolean rise){
        boolean fluid=RemnantAnimationState.fluid(entity),seated=RemnantAnimationState.riding(entity,riding);
        for(int i=0;i<legs.size();i++){
            var limb=legs.get(i);float phase=swing*.85F+(legs.size()==4?(i==0||i==3?0:Mth.PI):i*Mth.PI);
            var target=targets[i].set(limb.rest);target.z+=Mth.cos(phase)*stride*Math.min(4.8F,limb.reach*.45F);
            target.y-=Math.max(0,Mth.sin(phase))*stride*Math.min(3.5F,limb.reach*.30F)+motion.air*(rise?Math.min(5,limb.reach*.45F):.8F);
            if(seated)target.y-=3;if(fluid){target.y-=1.3F;target.z+=Mth.sin(t*.18F+i)*1.5F;}
        }
        if(!fluid&&!seated&&motion.air<.05F)RemnantAnimationGrounding.lowerBody(body,hips,targets,reaches);
        Matrix4f inverse=new Matrix4f().translation(body.x,body.y,body.z).rotateZYX(body.zRot,body.yRot,body.xRot).scale(body.xScale,body.yScale,body.zScale).invert();
        Quaternionf parent=new Quaternionf().rotationZYX(body.zRot,body.yRot,body.xRot);
        for(int i=0;i<legs.size();i++){var leg=legs.get(i);var target=new Vector3f(targets[i]);inverse.transformPosition(target).sub(leg.upper.x,leg.upper.y,leg.upper.z);leg.solve(target,false,parent);}
        boolean crossbow=entity.getMainHandItem().getItem() instanceof CrossbowItem,bow=entity.getMainHandItem().getItem() instanceof BowItem;
        float draw=RemnantAnimationState.drawTicks(entity,t-entity.tickCount-Math.floorMod(entity.getUUID().hashCode(),251));
        for(int i=0;i<arms.size();i++){
            var arm=arms.get(i);var target=new Vector3f(arm.rest);boolean active=i==(side>0?0:1);float sign=i==0?-1:1;
            target.z+=Mth.cos(swing*.85F+i*Mth.PI)*stride*4.5F;target.y-=idle*breath*.55F+motion.air*2;
            if(active){target.y-=wind*8.0F;target.z+=wind*4-hit*8;target.x-=sign*hit*3;}else{target.y-=wind*2;target.z-=hit*1.8F;}
            if((crossbow||bow)&&(draw>=0||entity.isAggressive()||attackTime>0)){
                int main=entity.getMainArm()==HumanoidArm.RIGHT?0:1;float mirror=main==0?1:-1,pull=draw>=0?smooth((draw-8)/20):wind;
                target.set((i==main?-4.5F:crossbow?1.8F:Mth.lerp(pull,-2,2))*mirror,body.getInitialPose().y-12.5F,i==main?-9:crossbow?-7.5F:Mth.lerp(pull,-8,-3.5F));
                if(crossbow&&i!=main){target.x-=pull*3.4F*mirror;target.z+=pull*2.3F;}
                target.rotateY(Mth.clamp(entity.yHeadRot-entity.yBodyRot,-45,45)*Mth.DEG_TO_RAD);
            }
            inverse.transformPosition(target).sub(arm.upper.x,arm.upper.y,arm.upper.z);arm.solve(target,true,parent);
        }
    }
    private void poseDragon(float t,float stride,float wind,float hit,float air,boolean rise,float land,float yaw,float pitch){
        float flap=Mth.sin(t*.18F),amplitude=.28F+air*.65F+stride*.2F;
        for(String side:List.of("left","right")){float sign=side.equals("left")?-1:1;
            turn(side+"_wing",.12F-Mth.cos(t*.18F)*.18F,-sign*.20F,sign*(flap*amplitude+wind*.4F-hit*.55F));
            turn(side+"_wing_tip",0,0,-sign*(Mth.sin(t*.18F+1.8F)*amplitude*.75F+wind*.15F));
            for(String pair:List.of("front","hind")){
                String name=side+"_"+pair+"_leg";float gait=Mth.cos(t*.16F+(sign>0?Mth.PI:0)+(pair.equals("hind")?Mth.PI:0))*stride*.45F;
                turn(name,gait+air*(rise?.75F:.25F)-land*.25F-wind*.18F+hit*.30F,0,sign*.06F);
                turn(name+"_tip",-air*.35F-gait*.55F+land*.35F,0,0);
            }
        }
        for(int i=0;i<5;i++)turn("neck_"+i,Mth.sin(t*.065F-i*.55F)*.055F+pitch*Mth.DEG_TO_RAD*.12F+wind*.075F-hit*.12F,
                Mth.sin(t*.045F-i*.5F)*.065F+yaw*Mth.DEG_TO_RAD*.08F,0);
        for(int i=0;i<12;i++)turn("tail_"+i,Mth.sin(t*.07F-i*.45F)*.045F+(rise?-air*.018F:air*.02F),Mth.sin(t*.055F-i*.5F)*(.075F+stride*.045F),0);
    }
    private static void rotate(ModelPart part,Quaternionf q){var a=q.getEulerAnglesZYX(new Vector3f());part.xRot=a.x;part.yRot=a.y;part.zRot=a.z;}
    @Override public void translateToHand(HumanoidArm side,PoseStack poses){
        if(arms.isEmpty())return;var arm=arms.get(side==HumanoidArm.RIGHT?0:1);
        body.translateAndRotate(poses);arm.upper.translateAndRotate(poses);arm.lower.translateAndRotate(poses);arm.end.translateAndRotate(poses);
        poses.translate((side==HumanoidArm.RIGHT?1:-1)/16.0F,-10/16.0F,2/16.0F);
    }
}
