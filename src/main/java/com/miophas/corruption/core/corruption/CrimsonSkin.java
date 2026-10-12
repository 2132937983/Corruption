package com.miophas.corruption.core.corruption;

import java.util.Random;

/** Deterministic, alpha-preserving skin processing; pixels use Minecraft's ABGR layout. */
public final class CrimsonSkin {
    public static final int CRACK_COLOR = 0x392356, BLOOD_COLOR = 0x4C3371, ACCENT_COLOR = 0x574088;
    public static int[] transform(int[] pixels, int width, int height, long seed) {
        if (width <= 0 || height <= 0 || (long) width * height != pixels.length)
            throw new IllegalArgumentException("Invalid skin dimensions");
        int[] result = pixels.clone();
        for (int i = 0; i < result.length; i++) {
            int pixel = pixels[i], alpha = pixel >>> 24;
            if (alpha == 0) continue;
            int r = pixel & 255, g = pixel >>> 8 & 255, b = pixel >>> 16 & 255;
            int luminance = (r * 2126 + g * 7152 + b * 722) / 10000;
            int red = 19 + (luminance * 32 + 127) / 255;
            int green = 17 + (luminance * 25 + 127) / 255, blue = 28 + (luminance * 35 + 127) / 255;
            result[i] = alpha << 24 | blue << 16 | green << 8 | red;
        }
        Random random = new Random(seed);
        int scale = Math.max(1, Math.min(width, height) / 64);
        int cracks = Math.max(6, Math.min(24, width * height / 1024));
        for (int crack = 0; crack < cracks; crack++) {
            int start = random.nextInt(pixels.length);
            // Select opaque skin rather than drawing into transparent UV padding.
            int attempts = 0;
            while (pixels[start] >>> 24 == 0 && attempts++ < pixels.length) start = (start + 1) % pixels.length;
            if (pixels[start] >>> 24 == 0) break;
            int x = start % width, y = start / width;
            int length = (4 + random.nextInt(5)) * scale;
            int[] xs = new int[length], ys = new int[length];
            for (int step = 0; step < length; step++) {
                if (step % scale == 0) x += random.nextInt(3) - 1;
                int sy = y + step;
                xs[step] = x; ys[step] = sy;
                // A jagged opening with dark edges, rather than a straight surface scratch.
                paint(result, width, height, x - scale, sy, 0x392548);
                for (int edge = 0; edge < scale; edge++) paint(result, width, height, x + edge, sy, CRACK_COLOR);
                if (step == length / 2) {
                    int direction = random.nextBoolean() ? 1 : -1;
                    for (int branch = 1; branch <= 3 * scale; branch++) paint(result, width, height, x + direction * branch, sy + branch / 2, CRACK_COLOR);
                }
            }
            // Blood starts inside the opening and follows connected downward pixels.
            for (int step = scale; step < length; step += 3 * scale) {
                int sx = xs[step], sy = ys[step];
                paint(result, width, height, sx, sy, BLOOD_COLOR);
                paint(result, width, height, sx, sy + scale, ACCENT_COLOR);
                if (random.nextBoolean()) paint(result, width, height, sx, sy + 2 * scale, CRACK_COLOR);
            }
        }
        return result;
    }

    /** Limbs, torso, then the whole head, with a stronger facial half and eyes completing last. */
    public static int[] playerProgress(int[] pixels, int width, int height, long seed, int value) {
        int[] complete = playerTransform(pixels, width, height, seed);
        int level = PlayerCorruptionRules.clamp(value);
        if (level == PlayerCorruptionRules.MAX) return complete;
        if (level == 0) return pixels.clone();
        int[] result = pixels.clone();float progress = level / (float) PlayerCorruptionRules.MAX;
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            int i=y*width+x;if (pixels[i] >>> 24 == 0) continue;
            int u=x*64/width,v=y*64/width;
            int faceU=u%32-8;
            boolean head=v<16,frontFace=faceU>=0&&faceU<8&&v>=8&&v<16;
            boolean eyes=v>=12&&v<=13&&(faceU==1||faceU==2||faceU==5||faceU==6);
            if (eyes) continue;
            long hash=seed ^ (x/Math.max(1,width/64)*73428767L) ^ (y/Math.max(1,width/64)*912931L);
            hash^=hash>>>13;hash*=0x5BD1E995L;hash^=hash>>>15;
            float noise=(hash&255)/255F;
            boolean torso=v>=16&&v<32&&u>=16&&u<40||v>=32&&v<48&&u>=16&&u<40;
            float threshold=(head?.60F:torso?.18F:.02F)+noise*(head?.14F:torso?.30F:.34F);
            float duration=head?.25F:.24F;
            if(frontFace){
                boolean firstHalf=(seed&1)==1?faceU<4:faceU>=4;
                // A stable side, with a slightly uneven edge; both hat and base face share the same mask.
                threshold=firstHalf?.65F+noise*.015F:.90F+noise*.03F;
                duration=firstHalf?.12F:.14F;
            }
            float blend=Math.clamp((progress-threshold)/duration,0,1);blend=blend*blend*(3-2*blend);
            int color=pixels[i]&0xFF000000;
            for(int channel=0;channel<3;channel++) {
                int a=pixels[i]>>>(channel*8)&255,b=complete[i]>>>(channel*8)&255;
                color|=Math.round(a+(b-a)*blend)<<(channel*8);
            }
            result[i]=color;
        }
        return result;
    }

    /** Player UV accents and replacement eyes, separate from arbitrary mob UV layouts. */
    public static int[] playerTransform(int[] pixels,int width,int height,long seed){
        int[] result=transform(pixels,width,height,seed);
        Random random=new Random(seed^0x6B2342L);
        for(int dot=0;dot<90;dot++){
            int u=dot<18?8+random.nextInt(8):random.nextInt(64);
            int v=dot<18?8+random.nextInt(8):16+random.nextInt(Math.max(1,Math.min(64,height*64/width)-16));
            uvPaint(result,width,height,u,v,dot%3==0?ACCENT_COLOR:BLOOD_COLOR);
            if(dot%4==0)uvPaint(result,width,height,u,v+1,CRACK_COLOR);
        }
        // Common player face eye row: two uneven dark-red eyes with restrained bright centers.
        for(int offset:new int[]{0,32})for(int u:new int[]{9,10,13,14}){
            uvPaint(result,width,height,u+offset,12,CRACK_COLOR);
            uvPaint(result,width,height,u+offset,13,u==10||u==13?0x62459B:ACCENT_COLOR);
        }
        return result;
    }
    private static void uvPaint(int[] pixels,int width,int height,int u,int v,int color){
        int x0=u*width/64,x1=Math.max(x0+1,(u+1)*width/64),y0=v*width/64,y1=Math.max(y0+1,(v+1)*width/64);
        for(int y=y0;y<y1;y++)for(int x=x0;x<x1;x++)paint(pixels,width,height,x,y,color);
    }

    private static void paint(int[] pixels, int width, int height, int x, int y, int abgr) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        int at = y * width + x;
        if (pixels[at] >>> 24 != 0) pixels[at] = pixels[at] & 0xFF000000 | abgr;
    }

    private CrimsonSkin() {}
}
