package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.GreedProjectile;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/** Three rigid circle/star pairs rotate around one sphere center; no texture baking per frame. */
public final class GreedProjectileRenderer extends EntityRenderer<GreedProjectile> {
    private static final int SEGMENTS=64;
    private static final float RADIUS=.32F;
    private static final float[] X=new float[SEGMENTS+1],Y=new float[SEGMENTS+1];
    static {for(int i=0;i<=SEGMENTS;i++){double a=i*Math.PI*2/SEGMENTS;X[i]=(float)Math.cos(a)*RADIUS;Y[i]=(float)Math.sin(a)*RADIUS;}}
    public GreedProjectileRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(GreedProjectile entity){return GreedRenderer.TEXTURE;}
    @Override public void render(GreedProjectile entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        float t=entity.tickCount+partial,phase=entity.getId()*.371F;
        VertexConsumer vertices=buffers.getBuffer(RenderType.lightning());
        for(int ring=0;ring<3;ring++) {
            poses.pushPose();
            poses.mulPose(new Quaternionf().rotationXYZ(t*(.026F+ring*.008F)+phase+ring*1.2F,
                    t*(.041F-ring*.005F)+ring*.9F,-t*(.019F+ring*.009F)+phase));
            Matrix4f matrix=poses.last().pose();
            for(int i=0;i<SEGMENTS;i++)line(vertices,matrix,X[i],Y[i],X[i+1],Y[i+1],.012F,191,92,119);
            for(int i=0;i<5;i++) {
                // First vertex is the downward tip; every second vertex forms the inverted pentagram.
                double a=-Math.PI/2+(i*2%5)*Math.PI*2/5,b=-Math.PI/2+((i+1)*2%5)*Math.PI*2/5;
                line(vertices,matrix,(float)Math.cos(a)*RADIUS,(float)Math.sin(a)*RADIUS,
                        (float)Math.cos(b)*RADIUS,(float)Math.sin(b)*RADIUS,.014F,156,52,81);
            }
            poses.popPose();
        }
        super.render(entity,yaw,partial,poses,buffers,light);
    }
    private static void line(VertexConsumer out,Matrix4f m,float ax,float ay,float bx,float by,float width,int r,int g,int b) {
        float dx=bx-ax,dy=by-ay,k=width/(float)Math.sqrt(dx*dx+dy*dy),nx=-dy*k,ny=dx*k;
        out.addVertex(m,ax+nx,ay+ny,0).setColor(r,g,b,220);
        out.addVertex(m,ax-nx,ay-ny,0).setColor(r,g,b,220);
        out.addVertex(m,bx-nx,by-ny,0).setColor(r,g,b,220);
        out.addVertex(m,bx+nx,by+ny,0).setColor(r,g,b,220);
    }
}
