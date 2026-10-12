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
public final class CorruptedWildlifeModel extends HierarchicalModel<Mob> implements ArmedModel {
    public enum Kind {
        ALLAY("allay"), ARMADILLO("armadillo"), AXOLOTL("axolotl"), BAT("bat"), BEE("bee"), BOGGED("bogged"), BREEZE("breeze"), CAMEL("camel"),
        CAT("cat"), CAVE_SPIDER("cave_spider"), COD("cod"), DOLPHIN("dolphin"), DROWNED("drowned"), ELDER_GUARDIAN("elder_guardian"), FOX("fox"),
        FROG("frog"), GLOW_SQUID("glow_squid"), GOAT("goat"), GUARDIAN("guardian"), HUSK("husk"), IRON_GOLEM("iron_golem"), LLAMA("llama"),
        MOOSHROOM("mooshroom"), MULE("mule"), OCELOT("ocelot"), PANDA("panda"), PARROT("parrot"), PHANTOM("phantom"), POLAR_BEAR("polar_bear"),
        PUFFERFISH("pufferfish"), RABBIT("rabbit"), SALMON("salmon"), SILVERFISH("silverfish"), SKELETON_HORSE("skeleton_horse"), SLIME("slime"),
        SNIFFER("sniffer"), SNOW_GOLEM("snow_golem"), SQUID("squid"), STRAY("stray"), TADPOLE("tadpole"), TRADER_LLAMA("trader_llama"),
        TROPICAL_FISH("tropical_fish"), TURTLE("turtle"), VEX("vex"), WANDERING_TRADER("wandering_trader"), WARDEN("warden"), WITCH("witch"),
        WOLF("wolf"), ZOMBIE_HORSE("zombie_horse");
        private static final java.util.Map<String, Kind> BY_ID = new java.util.HashMap<>();
        static { for (var kind : values()) BY_ID.put(kind.id, kind); }
        public final String id;
        public final ModelLayerLocation layer;
        private final CorruptedMobDefinition definition;
        Kind(String id){
            this.id=id;
            definition=CorruptedMobApi.require(CorruptedMobFamily.WILDLIFE,id);
            layer=new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"corrupted_"+id),definition.layerVariant());
        }
        public boolean flying(){return definition.flying();}
        public boolean humanoid(){return definition.humanoid();}
        public LayerDefinition createLayer(){return switch(this){
            case ALLAY -> CorruptedWildlifeGeometry.allay();case ARMADILLO -> CorruptedWildlifeGeometry.armadillo();case AXOLOTL -> CorruptedWildlifeGeometry.axolotl();
            case BAT -> CorruptedWildlifeGeometry.bat();case BEE -> CorruptedWildlifeGeometry.bee();case BOGGED -> CorruptedWildlifeGeometry.bogged();
            case BREEZE -> CorruptedWildlifeGeometry.breeze();case CAMEL -> CorruptedWildlifeGeometry.camel();case CAT -> CorruptedWildlifeGeometry.cat();
            case CAVE_SPIDER -> CorruptedWildlifeGeometry.cave_spider();case COD -> CorruptedWildlifeGeometry.cod();case DOLPHIN -> CorruptedWildlifeGeometry.dolphin();
            case DROWNED -> CorruptedWildlifeGeometry.drowned();case ELDER_GUARDIAN -> CorruptedWildlifeGeometry.elder_guardian();
            case FOX -> CorruptedWildlifeGeometry.fox();case FROG -> CorruptedWildlifeGeometry.frog();case GLOW_SQUID -> CorruptedWildlifeGeometry.glow_squid();
            case GOAT -> CorruptedWildlifeGeometry.goat();case GUARDIAN -> CorruptedWildlifeGeometry.guardian();case HUSK -> CorruptedWildlifeGeometry.husk();
            case IRON_GOLEM -> CorruptedWildlifeGeometry.iron_golem();case LLAMA -> CorruptedWildlifeGeometry.llama();
            case MOOSHROOM -> CorruptedWildlifeGeometry.mooshroom();case MULE -> CorruptedWildlifeGeometry.mule();case OCELOT -> CorruptedWildlifeGeometry.ocelot();
            case PANDA -> CorruptedWildlifeGeometry.panda();case PARROT -> CorruptedWildlifeGeometry.parrot();case PHANTOM -> CorruptedWildlifeGeometry.phantom();
            case POLAR_BEAR -> CorruptedWildlifeGeometry.polar_bear();case PUFFERFISH -> CorruptedWildlifeGeometry.pufferfish();
            case RABBIT -> CorruptedWildlifeGeometry.rabbit();case SALMON -> CorruptedWildlifeGeometry.salmon();case SILVERFISH -> CorruptedWildlifeGeometry.silverfish();
            case SKELETON_HORSE -> CorruptedWildlifeGeometry.skeleton_horse();case SLIME -> CorruptedWildlifeGeometry.slime();case SNIFFER -> CorruptedWildlifeGeometry.sniffer();
            case SNOW_GOLEM -> CorruptedWildlifeGeometry.snow_golem();case SQUID -> CorruptedWildlifeGeometry.squid();case STRAY -> CorruptedWildlifeGeometry.stray();
            case TADPOLE -> CorruptedWildlifeGeometry.tadpole();case TRADER_LLAMA -> CorruptedWildlifeGeometry.trader_llama();case TROPICAL_FISH -> CorruptedWildlifeGeometry.tropical_fish();
            case TURTLE -> CorruptedWildlifeGeometry.turtle();case VEX -> CorruptedWildlifeGeometry.vex();case WANDERING_TRADER -> CorruptedWildlifeGeometry.wandering_trader();
            case WARDEN -> CorruptedWildlifeGeometry.warden();case WITCH -> CorruptedWildlifeGeometry.witch();case WOLF -> CorruptedWildlifeGeometry.wolf();case ZOMBIE_HORSE -> CorruptedWildlifeGeometry.zombie_horse();};}
        public static Kind of(LivingEntity entity){return CorruptedMobKinds.of(CorruptedMobFamily.WILDLIFE,entity,BY_ID);}
    }
    private static final class Motion {
        float age,air,lastAttack,puff,landed=Float.NEGATIVE_INFINITY;
        boolean grounded;int attackIndex;
        Motion(Mob entity,float age){this.age=age;grounded=entity.onGround();puff=RemnantAnimationState.pufferState(entity);}
        void update(Mob entity,float now,float attack){
            float elapsed=Mth.clamp(now-age,0,4);
            if(entity.onGround()&&!grounded&&air>.1F)landed=now;
            grounded=entity.onGround();boolean airborne=!grounded&&!RemnantAnimationState.fluid(entity);
            air+=Mth.clamp((airborne?1:0)-air,-elapsed/3,elapsed/3);
            puff+=Mth.clamp(RemnantAnimationState.pufferState(entity)-puff,-elapsed/20,elapsed/8);
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
    public CorruptedWildlifeModel(ModelPart root,Kind kind){
        this.root=root;this.kind=kind;bones=CorruptedWildlifeGeometry.parts(root,kind.id);body=bones.get("corruption_root");all=root.getAllParts().toArray(ModelPart[]::new);
        for(String name:List.of("right_leg","left_leg","right_front_leg","left_front_leg","right_hind_leg","left_hind_leg","right_mid_leg","left_mid_leg"))
            if(bones.containsKey(name+"_lower"))legs.add(new Limb(name,false));
        if(kind.humanoid()&&kind!=Kind.ALLAY&&kind!=Kind.VEX){arms.add(new Limb("right_arm",true));arms.add(new Limb("left_arm",true));}
        hips=new Vector3f[legs.size()];targets=new Vector3f[legs.size()];reaches=new float[legs.size()];
        for(int i=0;i<legs.size();i++){var leg=legs.get(i);hips[i]=new Vector3f(leg.upper.x,leg.upper.y,leg.upper.z);targets[i]=new Vector3f();reaches[i]=leg.reach;}
    }
    @Override public ModelPart root(){return root;}
    private void turn(String name,float x,float y,float z){var part=bones.get(name);if(part!=null){part.xRot+=x;part.yRot+=y;part.zRot+=z;}}
    private static float smooth(float f){f=Mth.clamp(f,0,1);return f*f*(3-2*f);}
    @Override public void setupAnim(Mob entity,float swing,float amount,float age,float yaw,float pitch){
        for(var part:all)part.resetPose();var motion=motions.computeIfAbsent(entity,e->new Motion(e,age));motion.update(entity,age,attackTime);
        float t=age+Math.floorMod(entity.getUUID().hashCode(),251),breath=Mth.sin(t*.075F),shift=Mth.sin(t*.042F),stride=Mth.clamp(amount*1.65F,0,1);
        float idle=(1-stride*.75F)*(1-Mth.clamp(attackTime*5,0,1));
        float wind=attackTime<.3F?smooth(attackTime/.3F):attackTime<.65F?1-smooth((attackTime-.3F)/.35F):0;
        float hit=attackTime>=.3F&&attackTime<.65F?smooth((attackTime-.3F)/.35F):attackTime>=.65F?1-smooth((attackTime-.65F)/.35F):0;
        float land=1-Mth.clamp((age-motion.landed)/9,0,1),side=kind==Kind.IRON_GOLEM?1:(motion.attackIndex&1)==0?1:-1;
        boolean rise=entity.getDeltaMovement().y>.02;
        body.y+=idle*breath*(kind.flying()?.7F:.25F)+land*.7F+stride*.3F;
        body.xRot+=idle*breath*.045F+stride*.08F-wind*.15F+hit*.20F+motion.air*(rise?-.12F:.15F);
        body.zRot+=idle*shift*.055F+side*(wind*.08F-hit*.12F);
        body.yRot+=side*(-wind*.12F+hit*.20F);
        if(kind==Kind.POLAR_BEAR){body.xRot-=wind*.34F;body.xRot+=hit*.22F;}
        turn("head",idle*Mth.sin(t*.054F)*.10F+Mth.clamp(pitch*Mth.DEG_TO_RAD,-.4F,.5F)-wind*.18F+hit*.22F,
            idle*shift*.14F+Mth.clamp(yaw*Mth.DEG_TO_RAD,-.8F,.8F),idle*shift*.045F);
        for(var entry:bones.entrySet()){
            String name=entry.getKey();
            if(name.endsWith("_jaw"))turn(name,.10F+idle*Mth.sin(t*.085F-.5F)*.09F+wind*.45F+hit*.25F,0,0);
            if(name.contains("_finger_"))turn(name,idle*Mth.sin(t*.085F-name.hashCode()%5*.4F)*.12F+wind*.14F+land*.15F,0,0);
            if(name.endsWith("_claw"))turn(name,idle*breath*.045F-wind*.09F+hit*.14F,0,idle*shift*.06F);
            if(name.endsWith("_ear"))turn(name,0,0,Mth.sin(t*.14F+(name.startsWith("right")?0:1.5F))*.20F);
        }
        poseLimbs(entity,swing,stride,t,idle,breath,wind,hit,motion,side,rise);
        if(kind==Kind.ALLAY||kind==Kind.VEX){
            float flap=Mth.sin(t*.45F)*(.75F+stride*.25F);
            turn("right_wing",.22F,-.6F+flap,0);turn("left_wing",.22F,.6F-flap,0);
            turn("body",idle*breath*.06F+stride*.20F-wind*.12F+hit*.20F,0,0);
            for(String name:List.of("right_arm","left_arm")){
                float sign=name.startsWith("right")?1:-1;
                turn(name,.18F+Mth.sin(t*.12F+sign)*.20F+Mth.cos(swing*.85F+sign)*stride*.45F-wind*.60F+hit*.85F-motion.air*.20F,
                    sign*(wind*.12F-hit*.20F),sign*(.18F+breath*.09F));
                turn(name+"_lower",.20F+wind*.35F-hit*.30F,0,0);
            }
        }else if(kind==Kind.BAT){
            float flap=Mth.sin(t*.55F)*(.85F+stride*.25F);
            turn("right_wing",0,-.55F+flap,0);turn("left_wing",0,.55F-flap,0);
            turn("right_wing_tip",0,-Mth.sin(t*.55F-.8F)*.55F-wind*.20F,0);
            turn("left_wing_tip",0,Mth.sin(t*.55F-.8F)*.55F+wind*.20F,0);
            turn("feet",.35F+breath*.20F+wind*.35F-hit*.40F,0,0);
        }else if(kind==Kind.BEE){
            float flap=Mth.cos(t*1.6F)*(.65F+stride*.25F);
            turn("right_wing",.10F,0,flap);turn("left_wing",.10F,0,-flap);
            for(String name:List.of("front_legs","middle_legs","back_legs"))turn(name,.35F+Mth.sin(t*.16F+name.length())*.25F+wind*.35F-hit*.25F,0,0);
            turn("left_antenna",Mth.sin(t*.18F)*.20F-wind*.20F,0,.08F);
            turn("right_antenna",Mth.sin(t*.18F+1.2F)*.20F-wind*.20F,0,-.08F);
            turn("stinger_claw",wind*.35F-hit*.50F,0,0);
        }else if(kind==Kind.ARMADILLO){
            turn("tail",Mth.sin(t*.11F)*.25F+wind*.20F-hit*.35F,Mth.sin(t*.08F)*.25F,0);
            turn("head",wind*.25F-hit*.50F,0,0);
        }else if(kind==Kind.BREEZE){
            float spin=t*.14F;
            turn("rods",0,spin+wind*.5F-hit*.9F,0);
            String[] winds={"wind_bottom","wind_mid","wind_top"};
            for(int i=0;i<winds.length;i++){
                var part=bones.get(winds[i]);float phase=t*.16F-i*.9F;
                part.x+=Mth.sin(phase)*(.4F+i*.3F);part.z+=Mth.cos(phase)*(.4F+i*.3F);
                part.yRot+=spin*(i%2==0?1:-1);part.xRot+=Mth.sin(phase)*.09F+wind*.16F-hit*.20F;
            }
            turn("head",Mth.sin(t*.09F)*.12F-wind*.24F+hit*.30F,0,0);
        }else if(kind==Kind.CAMEL||kind==Kind.CAT||kind==Kind.OCELOT){
            turn("tail",Mth.sin(t*.11F)*.30F+wind*.22F-hit*.30F,Mth.sin(t*.08F)*.34F,0);
            turn("tail1",Mth.sin(t*.13F)*.20F,Mth.sin(t*.10F)*.38F+wind*.30F,0);
            turn("tail2",Mth.sin(t*.13F-1)*.36F,Mth.sin(t*.10F-1)*.30F,0);
            turn("body",idle*breath*.025F+stride*.035F-wind*.06F+hit*.08F,0,0);
            turn("head",wind*.20F-hit*.40F,0,Mth.sin(t*.07F)*idle*.08F);
        }else if(kind==Kind.CAVE_SPIDER){
            int i=0;for(String name:List.of("right_hind_leg","right_middle_hind_leg","right_middle_front_leg","right_front_leg","left_hind_leg","left_middle_hind_leg","left_middle_front_leg","left_front_leg")){
                float sign=i<4?-1:1,phase=swing*.9F+(i%2==0?0:Mth.PI),wave=Mth.sin(phase);
                turn(name,0,wave*stride*.38F+idle*Mth.sin(t*.10F+i)*.07F,
                    sign*(Math.max(0,Mth.cos(phase))*stride*.20F+wind*.09F-hit*.12F+motion.air*(rise?.22F:-.10F)+land*.12F));
                turn(name+"_knee",0,0,-sign*(Math.max(0,Mth.cos(phase))*stride*.28F+motion.air*.20F));i++;
            }
            turn("body1",idle*breath*.05F+wind*.12F-hit*.16F,0,0);
        }else if(kind==Kind.TADPOLE){
            float swim=RemnantAnimationState.fluid(entity)?Math.max(.4F,stride):stride;
            turn("tail",0,Mth.sin(t*.42F)*(.28F+swim*.52F)+wind*.28F-hit*.48F,0);
            turn("body",idle*breath*.08F,swim*Mth.sin(t*.42F)*.18F,shift*.08F);
        }else if(kind==Kind.TROPICAL_FISH){
            boolean large=entity instanceof net.minecraft.world.entity.animal.TropicalFish fish&&fish.getVariant().base()==net.minecraft.world.entity.animal.TropicalFish.Base.LARGE;
            for(int i=0;i<2;i++){
                bones.get("fish_state_"+i).visible=(i==1)==large;
                float swim=RemnantAnimationState.fluid(entity)?Math.max(.4F,stride):stride;
                turn("fish_"+i+"_tail",0,Mth.sin(t*.37F)*(.25F+swim*.55F)+wind*.26F-hit*.46F,0);
                turn("fish_"+i+"_left_fin",Mth.sin(t*.32F)*.28F,0,Mth.sin(t*.25F)*.30F);
                turn("fish_"+i+"_right_fin",-Mth.sin(t*.32F)*.28F,0,-Mth.sin(t*.25F)*.30F);
                turn("fish_"+i+"_body",idle*breath*.045F,Mth.sin(t*.37F)*swim*.16F,shift*.06F);
            }
        }else if(kind==Kind.TURTLE){
            float swim=RemnantAnimationState.fluid(entity)?1:stride;
            for(String sideName:List.of("right","left"))for(String pair:List.of("front","hind")){
                float sign=sideName.equals("right")?-1:1,phase=t*(.12F+swim*.18F)+(pair.equals("front")?0:1.3F)+(sign<0?0:1.6F);
                turn(sideName+"_"+pair+"_leg",Mth.cos(phase)*swim*.18F+motion.air*(rise?-.25F:.15F)+land*.18F,
                    sign*(Mth.sin(phase)*(.12F+swim*.60F)+wind*.35F-hit*.48F),sign*Mth.cos(phase)*swim*.10F);
            }
            turn("head",idle*breath*.12F+wind*.24F-hit*.38F,0,0);
        }else if(kind==Kind.COD){
            float swim=RemnantAnimationState.fluid(entity)?Math.max(.35F,stride):stride;
            turn("tail_fin",0,Mth.sin(t*.38F)*(.20F+swim*.55F)+wind*.25F-hit*.45F,0);
            turn("left_fin",Mth.sin(t*.32F)*.28F,0,Mth.sin(t*.22F)*.22F);
            turn("right_fin",-Mth.sin(t*.32F)*.28F,0,-Mth.sin(t*.22F)*.22F);
            body.yRot+=Mth.sin(t*.38F)*swim*.14F;
        }else if(kind==Kind.DOLPHIN){
            float swim=RemnantAnimationState.fluid(entity)?Math.max(.35F,stride):stride;
            turn("tail",Mth.sin(t*.29F)*(.16F+swim*.48F)+wind*.28F-hit*.40F,0,0);
            turn("tail_fin",Mth.sin(t*.29F-.9F)*(.16F+swim*.32F),0,0);
            turn("left_fin",Mth.sin(t*.19F)*.25F+wind*.25F,0,Mth.sin(t*.16F)*.22F);
            turn("right_fin",Mth.sin(t*.19F+1)*.25F+wind*.25F,0,-Mth.sin(t*.16F)*.22F);
            turn("body",Mth.sin(t*.29F)*swim*.12F,0,0);
        }else if(kind==Kind.DROWNED||kind==Kind.HUSK||kind==Kind.STRAY||kind==Kind.WITCH||kind==Kind.WANDERING_TRADER){
            turn("body",idle*breath*.07F+stride*.10F-wind*.16F+hit*.25F,0,idle*shift*.06F);
            turn("head",wind*.18F-hit*.30F,0,idle*Mth.sin(t*.08F)*.08F);
            if(kind==Kind.WITCH){turn("nose",Mth.sin(t*.12F)*.12F-wind*.18F,0,Mth.cos(t*.11F)*.10F);turn("hat2",idle*breath*.08F+wind*.12F-hit*.18F,0,shift*.06F);}
        }else if(kind==Kind.WARDEN){
            body.xRot+=idle*breath*.06F-wind*.22F+hit*.32F;body.yRot+=side*(-wind*.25F+hit*.38F);
            turn("body",idle*breath*.07F+stride*.08F-wind*.14F+hit*.22F,0,idle*shift*.08F);
            turn("head",idle*breath*.12F+wind*.22F-hit*.36F,shift*.12F,0);
            for(String name:List.of("right_tendril","left_tendril"))turn(name,breath*.22F,shift*.22F,Mth.sin(t*.14F)*.15F);
            turn("right_ribcage",idle*breath*.04F,breath*.08F,0);turn("left_ribcage",idle*breath*.04F,-breath*.08F,0);
        }else if(kind==Kind.IRON_GOLEM){
            body.xRot+=idle*breath*.035F+wind*.18F-hit*.28F;body.zRot+=idle*shift*.06F;
            body.yRot+=-wind*.18F+hit*.26F;
            turn("body",idle*breath*.07F-wind*.10F+hit*.16F,0,0);
            turn("head",idle*breath*.06F-wind*.20F+hit*.24F,0,0);
        }else if(kind==Kind.GUARDIAN||kind==Kind.ELDER_GUARDIAN){
            float swim=RemnantAnimationState.fluid(entity)?Math.max(.35F,stride):stride;
            for(int i=0;i<3;i++)turn("tail"+i,0,Mth.sin(t*.26F-i*.7F)*(.12F+swim*(.25F+i*.12F))+wind*.18F-hit*.30F,0);
            for(int i=0;i<12;i++){
                var spike=bones.get("spike"+i);if(spike!=null){
                    float pulse=1+Mth.sin(t*.12F+i*.6F)*.08F+wind*.20F-hit*.08F;
                    spike.xScale=spike.yScale=spike.zScale=pulse;
                }
            }
            body.y+=Mth.sin(t*.08F)*(.3F+swim*.4F);body.zRot+=Mth.sin(t*.09F)*.08F;
        }else if(kind==Kind.POLAR_BEAR){
            turn("head",idle*breath*.15F+wind*.22F-hit*.38F,0,idle*shift*.09F);
            turn("body",idle*breath*.035F,0,idle*shift*.03F);
        }else if(kind==Kind.PUFFERFISH){
            int state=motion.puff<.5F?0:motion.puff<1.5F?1:2;
            for(int i=0;i<3;i++){
                var form=bones.get("puffer_state_"+i);form.visible=i==state;
                float pulse=1+idle*breath*.035F+wind*.08F-hit*.045F;form.xScale=form.yScale=form.zScale=pulse;
                float flap=Mth.sin(t*.34F)*(.35F+stride*.30F)+wind*.20F-hit*.30F;
                String suffix=i==0?"_fin":"_blue_fin";
                turn("puffer_"+i+"_left"+suffix,.08F,0,-.22F-flap);
                turn("puffer_"+i+"_right"+suffix,.08F,0,.22F+flap);
                turn("puffer_"+i+"_back_fin",0,Mth.sin(t*.32F)*.32F,0);
            }
            body.yRot+=Mth.sin(t*.16F)*(.09F+stride*.16F);body.zRot+=Mth.sin(t*.12F)*.08F;
        }else if(kind==Kind.RABBIT){
            float hop=Math.max(0,Mth.sin(swing*.9F))*stride;
            turn("body",idle*breath*.075F-hop*.12F-wind*.10F+hit*.16F,0,idle*shift*.06F);
            for(String name:List.of("left","right")){
                turn(name+"_haunch",hop*.70F+motion.air*(rise?.65F:-.25F)+wind*.30F-hit*.45F+land*.25F,0,0);
                turn(name+"_hind_foot",-hop*.40F-motion.air*(rise?.45F:-.18F)-wind*.20F+hit*.30F,0,0);
            }
            turn("head",idle*breath*.16F+wind*.28F-hit*.42F,0,idle*shift*.10F);
            turn("right_ear",Mth.sin(t*.13F)*.20F+wind*.22F-hit*.28F,0,-idle*shift*.13F);
            turn("left_ear",Mth.sin(t*.13F-1)*.24F+wind*.22F-hit*.28F,0,idle*shift*.13F);
            turn("tail",Mth.sin(t*.18F)*.20F+land*.20F,shift*.18F,0);
        }else if(kind==Kind.SALMON){
            float swim=RemnantAnimationState.fluid(entity)?Math.max(.35F,stride):stride;
            turn("body_front",0,Mth.sin(t*.31F)*(.09F+swim*.14F)+wind*.12F-hit*.16F,0);
            turn("body_back",0,Mth.sin(t*.31F-.9F)*(.25F+swim*.45F)+wind*.28F-hit*.40F,0);
            turn("back_fin",0,Mth.sin(t*.31F-1.6F)*(.25F+swim*.32F),0);
            turn("left_fin",Mth.sin(t*.27F)*.25F,0,Mth.sin(t*.22F)*.30F);
            turn("right_fin",-Mth.sin(t*.27F)*.25F,0,-Mth.sin(t*.22F)*.30F);
            body.zRot+=idle*breath*.045F+land*.12F;
        }else if(kind==Kind.SILVERFISH){
            for(int i=0;i<7;i++){
                float phase=t*(.16F+stride*.30F)-i*.80F;
                turn("segment"+i,Mth.sin(phase*.6F)*.035F+wind*.07F-hit*.10F,
                    Mth.sin(phase)*(.09F+stride*.16F)+side*(wind*.07F-hit*.11F),Mth.cos(phase)*.035F);
            }
            turn("segment0",Mth.clamp(pitch*Mth.DEG_TO_RAD,-.2F,.25F),Mth.clamp(yaw*Mth.DEG_TO_RAD,-.3F,.3F),0);
        }else if(kind==Kind.MOOSHROOM||kind==Kind.MULE||kind==Kind.PANDA||kind==Kind.SKELETON_HORSE||kind==Kind.ZOMBIE_HORSE){
            turn("head",idle*Mth.sin(t*.08F)*.16F+wind*.38F-hit*.62F,0,idle*shift*.10F);
            turn("head_parts",idle*breath*.13F+wind*.28F-hit*.48F,idle*shift*.10F,0);
            turn("tail",Mth.sin(t*.12F)*.28F+wind*.20F,Mth.sin(t*.10F)*.40F,0);
            turn("body",idle*breath*.045F+stride*.07F-wind*.12F+hit*.18F,0,idle*shift*.045F);
            if(kind==Kind.MOOSHROOM)for(int i=0;i<2;i++)
                turn("corrupt_mushroom_"+i,Mth.sin(t*.095F-i*.9F)*.12F+wind*.16F-hit*.22F,0,Mth.sin(t*.07F-i)*.10F);
        }else if(kind==Kind.PARROT){
            float flap=Mth.sin(t*.48F)*(.50F+stride*.45F+motion.air*.20F);
            turn("left_wing",.22F+wind*.40F-hit*.55F,0,-.40F-flap+wind*.35F-hit*.65F);
            turn("right_wing",.22F+wind*.40F-hit*.55F,0,.40F+flap-wind*.35F+hit*.65F);
            turn("tail",.38F+breath*.20F+wind*.40F-hit*.50F+land*.25F,0,shift*.14F);
            turn("feather",breath*.20F-wind*.20F+hit*.32F,0,shift*.16F);
            turn("head",idle*breath*.15F+wind*.28F-hit*.50F,0,idle*shift*.12F);
        }else if(kind==Kind.PHANTOM){
            float flap=Mth.sin(t*.23F)*(.55F+stride*.30F),tip=Mth.sin(t*.23F-.8F)*(.70F+stride*.35F);
            for(String name:List.of("left","right")){
                float sign=name.equals("left")?1:-1;
                turn(name+"_wing_base",wind*.20F-hit*.28F,0,sign*(flap+wind*.40F-hit*.60F+land*.25F));
                turn(name+"_wing_tip",0,sign*wind*.15F,sign*(tip+wind*.45F-hit*.55F));
            }
            turn("tail_base",Mth.sin(t*.16F)*.25F+wind*.32F-hit*.45F,shift*.12F,0);
            turn("tail_tip",Mth.sin(t*.16F-.9F)*.38F+wind*.25F-hit*.35F,shift*.18F,0);
            turn("head",idle*breath*.12F+wind*.28F-hit*.45F,0,0);
        }else if(kind==Kind.FOX||kind==Kind.GOAT||kind==Kind.LLAMA||kind==Kind.TRADER_LLAMA||kind==Kind.WOLF){
            turn("head",idle*Mth.sin(t*.07F)*.10F+wind*.30F-hit*.50F,0,idle*shift*.07F);
            turn("tail",Mth.sin(t*.12F)*.25F,Mth.sin(t*.10F)*.32F+wind*.18F,0);
            turn("body",idle*breath*.035F+stride*.05F-wind*.10F+hit*.14F,0,0);
            turn("nose",idle*breath*.05F+wind*.12F-hit*.18F,0,0);
            if(kind==Kind.WOLF){turn("wolf_upper_jaw",idle*breath*.12F-wind*.14F+hit*.18F,shift*.09F,shift*.06F);turn("real_tail",breath*.28F,Mth.sin(t*.13F)*.35F,0);turn("head",-wind*.22F+hit*.35F,0,0);}
        }else if(kind==Kind.FROG){
            float hop=Mth.cos(swing*.95F)*stride;
            for(String name:List.of("right_leg","left_leg")){
                turn(name,hop*.75F+motion.air*(rise?-.65F:.40F)+land*.35F+wind*.25F-hit*.40F,0,0);
                turn(name.replace("leg","foot"),-hop*.35F-motion.air*.25F,0,0);
            }
            turn("right_arm",-hop*.45F-wind*.45F+hit*.75F,0,-idle*breath*.08F);
            turn("left_arm",-hop*.45F-wind*.45F+hit*.75F,0,idle*breath*.08F);
            var throat=bones.get("croaking_body");if(throat!=null){throat.visible=true;throat.yScale=1+idle*(.12F+breath*.11F)+wind*.25F;}
            var tongue=bones.get("tongue");if(tongue!=null){tongue.visible=hit>.05F;tongue.zScale=1+hit*2.8F;}
        }else if(kind==Kind.GLOW_SQUID||kind==Kind.SQUID){
            float swim=RemnantAnimationState.fluid(entity)?1:stride;
            for(int i=0;i<8;i++){
                turn("tentacle"+i,.20F+Mth.sin(t*.20F+i*.65F)*(.30F+swim*.18F)+wind*.40F-hit*.65F,0,0);
                turn("tentacle"+i+"_middle",Mth.sin(t*.20F+i*.65F-.8F)*.40F+wind*.35F-hit*.40F,0,0);
                turn("tentacle"+i+"_tip",Mth.sin(t*.20F+i*.65F-1.6F)*.45F+wind*.25F-hit*.30F,0,0);
            }
            body.xRot+=swim*.20F+idle*breath*.07F;body.yRot+=Mth.sin(t*.06F)*.12F;
            if(kind==Kind.SQUID)turn("crown_spine",idle*Mth.sin(t*.09F)*.12F+wind*.30F-hit*.48F,shift*.12F,0);
        }else if(kind==Kind.SLIME){
            float crawl=Mth.sin(t*.32F)*stride,squash=idle*breath*.055F+land*.32F-motion.air*(rise?.16F:-.05F)+wind*.12F-hit*.16F;
            var puddle=bones.get("cube");puddle.xScale=1+squash-crawl*.14F;puddle.zScale=1+squash+crawl*.19F;puddle.yScale=1-squash*.65F;
            puddle.y-=motion.air*(rise?.65F:.25F);body.y=body.getInitialPose().y;body.xRot=idle*breath*.015F-wind*.04F+hit*.04F;body.zRot=idle*shift*.018F;
            for(int i=0;i<6;i++){
                String name=i==5?"blood_claw_center":"blood_claw_"+i;
                turn(name,Mth.sin(t*(.12F+stride*.20F)-i*.7F)*(.23F+stride*.18F)-wind*.48F+hit*.72F+motion.air*(rise?-.28F:.20F)+land*.32F,
                    Mth.sin(t*.09F-i)*.18F+side*(wind*.18F-hit*.28F),Mth.sin(t*.10F-i*.8F)*.18F);
            }
        }else if(kind==Kind.SNIFFER){
            turn("body",idle*breath*.04F+stride*.06F-wind*.12F+hit*.18F,0,idle*shift*.045F);
            turn("head",idle*breath*.14F+stride*.12F+wind*.28F-hit*.48F,0,idle*shift*.10F);
            turn("nose",breath*.10F+wind*.20F-hit*.28F,0,shift*.07F);
            turn("lower_beak",.12F+idle*Mth.sin(t*.10F)*.13F+wind*.42F+hit*.22F,0,0);
        }else if(kind==Kind.SNOW_GOLEM){
            turn("upper_body",idle*breath*.13F+stride*.18F-wind*.24F+hit*.36F,side*(wind*.30F-hit*.42F),idle*shift*.12F);
            turn("lower_body",idle*breath*.025F+land*.09F,0,-idle*shift*.045F);
            for(String name:List.of("right_arm","left_arm")){
                float sign=name.startsWith("right")?-1:1;
                turn(name,idle*Mth.sin(t*.13F+sign)*.14F-wind*.55F+hit*.80F+motion.air*(rise?-.35F:.18F),
                    sign*(stride*.25F+wind*.40F-hit*.68F),sign*(idle*breath*.18F+land*.22F));
                turn(sign<0?"pumpkin_right_half":"pumpkin_left_half",idle*breath*.04F+land*.10F,sign*(idle*breath*.08F+wind*.12F),sign*shift*.04F);
            }
        }else if(kind==Kind.AXOLOTL){
            boolean fluid=RemnantAnimationState.fluid(entity);float swim=fluid?1:stride;
            for(String name:List.of("flesh_core","flesh_front","flesh_rear","flesh_crown")){
                float phase=t*.10F+name.hashCode()%7*.6F;var part=bones.get(name);
                part.xRot+=Mth.sin(phase)*.07F-wind*.08F+hit*.12F;
                part.zRot+=Mth.cos(phase*.7F)*.08F;
                part.yScale=1+Mth.sin(phase)*.045F;part.xScale=part.zScale=1-Mth.sin(phase)*.025F;
            }
            turn("tail",Mth.sin(t*.11F)*.12F,Mth.sin(t*(fluid?.32F:.16F))*(.25F+swim*.35F),0);
            for(String name:List.of("top_gills","left_gills","right_gills"))turn(name,Mth.sin(t*.17F+name.length())*.20F,0,Mth.cos(t*.12F+name.length())*.16F);
            int i=0;for(String name:List.of("right_front_leg","left_front_leg","right_hind_leg","left_hind_leg")){
                float wave=Mth.sin(swing*.85F+(i==0||i==3?0:Mth.PI));
                turn(name,-.3F+wave*stride*.60F+motion.air*(rise?-.55F:.30F)+wind*.25F-hit*.35F,
                    Mth.sin(t*.24F+i)*swim*.35F,(i%2==0?1:-1)*(.18F+land*.25F));i++;
            }
        }
    }
    private void poseLimbs(Mob entity,float swing,float stride,float t,float idle,float breath,float wind,float hit,Motion motion,float side,boolean rise){
        boolean fluid=RemnantAnimationState.fluid(entity),seated=RemnantAnimationState.riding(entity,riding);
        for(int i=0;i<legs.size();i++){
            var limb=legs.get(i);float phase=swing*.85F+(legs.size()==4?(i==0||i==3?0:Mth.PI):legs.size()==6?(i==0||i==2||i==5?0:Mth.PI):i*Mth.PI);
            var target=targets[i].set(limb.rest);target.z+=Mth.cos(phase)*stride*Math.min(4.8F,limb.reach*.45F);
            target.y-=Math.max(0,Mth.sin(phase))*stride*Math.min(3.5F,limb.reach*.30F)+motion.air*(rise?Math.min(5,limb.reach*.45F):.8F);
            if(kind==Kind.POLAR_BEAR&&limb.name.contains("front")){target.y-=wind*7+hit*4;target.z-=hit*6;}
            if(kind==Kind.RABBIT){target.y-=wind*2+hit*1.5F;target.z-=hit*2;}
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
            if(kind==Kind.IRON_GOLEM){
                if(i==0){
                    // The heavy mutated arm leads every attack: lift outward, then sweep forward and down.
                    target.y-=wind*12+hit*10;target.z+=wind*3-hit*14;
                    target.x-=wind*6;target.x+=hit*2;
                }else{target.y-=wind*3+hit*2;target.z-=hit*2;}
            }
            if(kind==Kind.WARDEN){
                if(active){target.y-=wind*22+hit*12;target.z+=wind*10-hit*25;target.x+=sign*(wind*7-hit*5);}
                else{target.y-=wind*7;target.z-=hit*5;}
                // The long fingertips extend beyond the wrist; leave their full length above the floor.
                target.y=Math.min(target.y,4.0F);
            }
            if((crossbow||bow)&&(draw>=0||entity.isAggressive()||attackTime>0)){
                int main=entity.getMainArm()==HumanoidArm.RIGHT?0:1;float mirror=main==0?1:-1,pull=draw>=0?smooth((draw-8)/20):wind;
                target.set((i==main?-4.5F:crossbow?1.8F:Mth.lerp(pull,-2,2))*mirror,body.getInitialPose().y-12.5F,i==main?-9:crossbow?-7.5F:Mth.lerp(pull,-8,-3.5F));
                if(crossbow&&i!=main){target.x-=pull*3.4F*mirror;target.z+=pull*2.3F;}
                target.rotateY(Mth.clamp(entity.yHeadRot-entity.yBodyRot,-45,45)*Mth.DEG_TO_RAD);
            }
            inverse.transformPosition(target).sub(arm.upper.x,arm.upper.y,arm.upper.z);arm.solve(target,true,parent);
            if(kind==Kind.IRON_GOLEM&&i==0)turn("right_arm_hand",-wind*.40F+hit*.28F,wind*.32F-hit*.48F,wind*.12F-hit*.18F);
        }
    }
    private static void rotate(ModelPart part,Quaternionf q){var a=q.getEulerAnglesZYX(new Vector3f());part.xRot=a.x;part.yRot=a.y;part.zRot=a.z;}
    @Override public void translateToHand(HumanoidArm side,PoseStack poses){
        if(kind==Kind.ALLAY||kind==Kind.VEX){
            String name=side==HumanoidArm.RIGHT?"right_arm":"left_arm";
            body.translateAndRotate(poses);bones.get("root").translateAndRotate(poses);bones.get("body").translateAndRotate(poses);
            bones.get(name).translateAndRotate(poses);bones.get(name+"_lower").translateAndRotate(poses);bones.get(name+"_hand").translateAndRotate(poses);
            poses.translate((side==HumanoidArm.RIGHT?1:-1)/16.0F,-10/16.0F,2/16.0F);return;
        }
        if(arms.isEmpty())return;var arm=arms.get(side==HumanoidArm.RIGHT?0:1);
        body.translateAndRotate(poses);arm.upper.translateAndRotate(poses);arm.lower.translateAndRotate(poses);arm.end.translateAndRotate(poses);
        poses.translate((side==HumanoidArm.RIGHT?1:-1)/16.0F,-10/16.0F,2/16.0F);
    }
}
