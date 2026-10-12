package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Greed;
import com.miophas.corruption.api.corruption.GreedApi;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class GreedModel extends HierarchicalModel<Greed> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"greed"),"main");
    private final ModelPart root;
    private final ModelPart[] plants=new ModelPart[5], parts;
    private final ModelPart[][] branches=new ModelPart[5][3],heads=new ModelPart[5][3],jaws=new ModelPart[5][3];
    private final ModelPart[][][] necks=new ModelPart[5][3][];
    public GreedModel(ModelPart root) {
        this.root=root;parts=root.getAllParts().toArray(ModelPart[]::new);
        for(int s=0;s<5;s++) {
            String prefix="stage_"+s;plants[s]=root.getChild(prefix);
            for(int h=0;h<3;h++) {
                branches[s][h]=plants[s].getChild(prefix+"_branch_"+h);
                necks[s][h]=new ModelPart[GreedApi.neckSegments(s,h)];
                ModelPart parent=branches[s][h];
                for(int i=0;i<necks[s][h].length;i++) {
                    parent=parent.getChild(prefix+"_neck_"+h+(i==0?"":"_joint_"+i));
                    necks[s][h][i]=parent;
                }
                heads[s][h]=parent.getChild(prefix+"_head_"+h);
                jaws[s][h]=heads[s][h].getChild(prefix+"_jaw_"+h);
            }
        }
    }
    @Override public ModelPart root(){return root;}
    public static LayerDefinition createBodyLayer(){return GreedGeometry.createBodyLayer();}
    @Override public void setupAnim(Greed entity,float swing,float amount,float age,float yaw,float pitch) {
        for(ModelPart part:parts)part.resetPose();int s=entity.stage();
        for(int i=0;i<5;i++)plants[i].visible=i==s;
        float t=age+Math.floorMod(entity.getUUID().hashCode(),71);
        plants[s].xRot+=Mth.sin(t*.025F)*.040F;plants[s].zRot+=Mth.sin(t*.019F)*.050F;
        float shot=entity.shotAge(age-entity.tickCount);
        for(int h=0;h<3;h++) {
            float phase=t+h*18;
            branches[s][h].zRot+=Mth.sin(phase*.037F)*.090F;
            branches[s][h].xRot+=Mth.sin(phase*.028F+.8F)*.045F;
            ModelPart[] chain=necks[s][h];
            float flexibility=(1+s*.12F)/chain.length;
            for(int i=0;i<chain.length;i++) {
                chain[i].xRot+=Mth.sin(phase*.041F-i*.52F-.6F)*(h==2?.13F:.075F)*flexibility;
                chain[i].yRot+=Mth.sin(phase*.027F-i*.44F+.4F)*(h==2?.16F:.10F)*flexibility;
                chain[i].zRot+=Mth.sin(phase*.035F-i*.58F+.8F)*(h==2?.085F:.045F)*flexibility;
            }
            heads[s][h].yRot+=Mth.sin(phase*.032F)*(h==2?.36F:h==0?.26F:.22F);
            heads[s][h].xRot+=Mth.sin(phase*.043F)*(h==2?.18F:.12F);
            heads[s][h].zRot+=Mth.sin(phase*.035F+.9F)*(h==2?.09F:.045F);
            jaws[s][h].xRot+=(h==2?.19F:.10F)+(.5F+.5F*Mth.sin(phase*.061F))*(h==1?.21F:.18F);
            if(h==entity.attackingHead()&&shot>=0&&shot<20) {
                float windup=Mth.clamp(shot/8,0,1), recovery=1-Mth.clamp((shot-8)/12,0,1);
                float open=shot<8?windup:recovery;
                jaws[s][h].xRot+=open*(h==2?.85F:h==0?.75F:.55F);
                heads[s][h].xRot-=shot<8?windup*.24F:recovery*.40F;
                for(int i=0;i<chain.length;i++) {
                    float weight=2F*(i+1)/(chain.length*(chain.length+1));
                    chain[i].xRot+=(shot<8?windup*.10F:-recovery*.13F)*weight;
                }
                branches[s][h].xRot+=shot<8?windup*.075F:-recovery*.10F;
            }
        }
    }
}
