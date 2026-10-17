package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Amdusias;
import com.miophas.corruption.core.demon.AmdusiasRules;
import com.miophas.corruption.core.demon.AmdusiasRules.Skill;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Amdusias, the unicorn. His geometry is data (see {@link GeoModel}, made by gamemodels/amdusias/amdusias_model.py); this
 * finds the bones that move and poses them. He gallops as Seere does, and rears, pawing the air, for the blows that are
 * Seere's; for his own: he rears and comes down on all four to blight the ground; he lowers his horn to the ground and
 * paws at it as he heaves it up under his quarry; he lifts his head, his horn swelling with light, to strike at its
 * mind; he throws his head up and back and sounds his fanfare, his jaw open, his head jolting with every blast; and he
 * rears and stamps once for every ring of his crescendo.
 *
 * <p>A skill goes on being posed after it is over, until he begins another, and every pose eases back to rest by itself.
 * As in every model of this space, -Y is up and -Z before him: a positive X turn swings a hanging leg's hoof back, and
 * tips his body nose down.
 */
public final class AmdusiasModel extends HierarchicalModel<Amdusias> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "amdusias"), "main");
    static final String GEOMETRY = "amdusias";
    /** How long a stride of his gallop is, in the distance he has run, and how high he bounds. */
    static final float STRIDE = .42F, BOUND = 1.3F;

    // Seen by AmdusiasModelTest, which poses the baked model and looks at where the bones went.
    final ModelPart root, body, neck, neckUpper, head, horn, jaw, mane, maneUpper, tail, tailMiddle, tailEnd;
    final ModelPart[] all;
    /** Left then right: upper leg, lower leg, hoof. */
    final ModelPart[] front = new ModelPart[2], frontLower = new ModelPart[2], frontHoof = new ModelPart[2];
    final ModelPart[] hind = new ModelPart[2], hindLower = new ModelPart[2], hindHoof = new ModelPart[2];

    public AmdusiasModel(ModelPart root) {
        this.root = root;
        Map<String, ModelPart> bones = GeoModel.load(GEOMETRY).parts(root);
        all = bones.values().toArray(ModelPart[]::new);
        body = need(bones, "body"); neck = need(bones, "neck"); neckUpper = need(bones, "neck_1"); head = need(bones, "head"); horn = need(bones, "horn");
        jaw = need(bones, "jaw"); mane = need(bones, "mane"); maneUpper = need(bones, "mane_1"); tail = need(bones, "tail");
        tailMiddle = need(bones, "tail_1"); tailEnd = need(bones, "tail_2");
        String[] sides = {"left", "right"};
        for (int side = 0; side < 2; side++) {
            front[side] = need(bones, "leg_front_" + sides[side]);
            frontLower[side] = need(bones, "leg_front_" + sides[side] + "_1");
            frontHoof[side] = need(bones, "hoof_front_" + sides[side]);
            hind[side] = need(bones, "leg_hind_" + sides[side]);
            hindLower[side] = need(bones, "leg_hind_" + sides[side] + "_1");
            hindHoof[side] = need(bones, "hoof_hind_" + sides[side]);
        }
    }

    private static ModelPart need(Map<String, ModelPart> bones, String name) {
        ModelPart bone = bones.get(name);
        if (bone == null) throw new IllegalStateException("Amdusias's geometry has no bone " + name);
        return bone;
    }

    public static LayerDefinition createBodyLayer() { return GeoModel.load(GEOMETRY).layer(); }

    @Override public ModelPart root() { return root; }

    static float ease(float amount) { amount = Mth.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    @Override public void setupAnim(Amdusias amdusias, float swing, float amount, float age, float yaw, float pitch) {
        float partial = age - amdusias.tickCount;
        float dying = amdusias.deathTime > 0 ? Mth.clamp((amdusias.deathTime + partial) / AmdusiasRules.DEATH_TICKS, 0, 1) : 0;
        pose(age, swing, amount, amdusias.posedSkill(), amdusias.skillTicks(partial), dying, amdusias.getId());
    }

    /**
     * @param age    ticks, for the motion that never stops
     * @param swing  how far he has run, as any creature's walk is counted, and {@code amount} how fast he runs now
     * @param skill  what he is doing (or last did), and {@code ticks} since he began it
     * @param dying  0 alive to 1 dead
     * @param phase  his own, so that no two of him move together
     */
    public void pose(float age, float swing, float amount, Skill skill, float ticks, float dying, int phase) {
        for (ModelPart part : all) {
            part.resetPose();
            part.xScale = part.yScale = part.zScale = 1;
        }
        float time = age + phase * 4.7F, run = Mth.clamp(amount, 0, 1);

        // ---- at rest, and his gallop ----
        float breath = Mth.sin(time * .08F);
        body.y += .3F * breath;
        neck.xRot += .04F * Mth.sin(time * .05F);
        head.xRot += .05F * Mth.sin(time * .05F + 1);
        tail.zRot += .15F * Mth.sin(time * .07F);
        tailMiddle.zRot += .2F * Mth.sin(time * .07F - .8F);
        float stride = swing * STRIDE;
        // A rotary gallop: the hind legs a little apart, the forelegs half a stride after them.
        float[] hindAt = {0, .35F}, frontAt = {Mth.PI, Mth.PI + .35F};
        for (int side = 0; side < 2; side++) {
            float h = Mth.sin(stride + hindAt[side]), f = Mth.sin(stride + frontAt[side]);
            hind[side].xRot += .75F * h * run;
            hindLower[side].xRot += -.6F * Math.max(0, -h) * run;
            front[side].xRot += .8F * f * run;
            frontLower[side].xRot += 1.1F * Math.max(0, f) * run;
            frontHoof[side].xRot += .4F * Math.max(0, f) * run;
        }
        body.y -= BOUND * Math.abs(Mth.sin(stride)) * run;
        body.xRot += .07F * Mth.sin(stride + .5F) * run;
        neck.xRot += .12F * Mth.sin(stride + 1.2F) * run + .1F * run;
        tail.xRot += .9F * run;
        mane.xRot += .3F * run;
        maneUpper.xRot += .3F * run;
        // His horn's light swells and fades, slowly.
        float glow = 1 + .03F * Mth.sin(time * .11F);

        // ---- what he is doing ----
        float windup = windup(skill), length = length(skill);
        float rear = 0, crouch = 0, lunge = 0, paw = 0, mouth = 0, lift = 0, lower = 0;
        switch (skill) {
            case HOOVES -> {
                // Rearing up, forehooves pawing the air, then down on them.
                rear = ease(ticks / (windup - 2)) * (1 - ease((ticks - windup) / 4));
                paw = rear;
                mouth = rear;
            }
            case SPIKES, BLIGHT -> {
                // Rearing high, then down on the ground with all his weight.
                rear = 1.2F * ease(ticks / (windup - 4)) * (1 - ease((ticks - windup + 1) / 3));
                paw = .6F * rear;
                mouth = rear;
                crouch = .7F * ease((ticks - windup) / 2) * (1 - ease((ticks - windup - 4) / 8));
            }
            case BLINKS -> {
                // Crouched to spring, then flung out long between his blinks.
                crouch = ease(ticks / windup) * (1 - ease((ticks - windup) / 3));
                lunge = ease((ticks - windup) / 2) * (1 - ease((ticks - length) / 6));
            }
            case UPHEAVAL -> {
                // His horn lowered to the ground, a forehoof pawing at it; then a stamp as it heaves.
                lower = ease(ticks / 6) * (1 - ease((ticks - length) / 8));
                front[0].xRot -= .6F * lower * Math.max(0, Mth.sin(ticks * .9F)) * (ticks < windup ? 1 : 0);
                crouch = .5F * ease((ticks - windup) / 2) * (1 - ease((ticks - windup - 3) / 6));
            }
            case PSYCHIC -> {
                // His head up, his horn swelling with light; a jolt as he strikes.
                lift = ease(ticks / 6) * (1 - ease((ticks - length) / 8));
                glow += .25F * lift * (ticks < windup ? ease(ticks / windup) : 1 - ease((ticks - windup) / 4));
            }
            case FANFARE -> {
                // His head thrown up and back, his jaw open; braced; his head jolting at every blast.
                lift = 1.4F * ease(ticks / 10) * (1 - ease((ticks - length) / 10));
                mouth = lift * .8F;
                crouch = .25F * lift;
                if (ticks >= windup && ticks < length) head.xRot += .06F * Mth.sin(ticks * 1.9F);
                glow += .15F * lift;
            }
            case CRESCENDO -> {
                // He rears and stamps once for every ring.
                float rings = AmdusiasRules.CRESCENDO.rings(), every = AmdusiasRules.CRESCENDO.every();
                float stamps = Math.max(0, ticks - windup), cycle = stamps % every;
                float before = ticks < windup ? ease(ticks / (windup - 2)) : stamps < every * rings ? 1 - ease(cycle / 3) + ease((cycle - 4) / (every - 4)) * .6F : 0;
                rear = 1.1F * Mth.clamp(before, 0, 1) * (1 - ease((ticks - length) / 6));
                paw = .5F * rear;
                mouth = rear;
                lift = .5F * rear;
            }
            default -> {}
        }

        // Rearing turns him up about his hind legs: up at the front, the hind legs keeping their hooves under him.
        if (rear > 0) {
            body.xRot -= .85F * rear;
            body.y -= 6 * rear;
            body.z += 3 * rear;
            for (int side = 0; side < 2; side++) {
                hind[side].xRot += .85F * rear;
                hindLower[side].xRot -= .3F * rear;
                float pawing = paw * Mth.sin(ticks * 1.1F + side * Mth.PI);
                front[side].xRot += -1.1F * rear + .45F * pawing;
                frontLower[side].xRot += 1.2F * rear - .5F * pawing;
            }
            neck.xRot -= .35F * rear;
            head.xRot += .45F * rear;
        }
        if (crouch > 0) {
            body.y += 3 * crouch;
            for (int side = 0; side < 2; side++) {
                front[side].xRot -= .35F * crouch;
                frontLower[side].xRot += .7F * crouch;
                hind[side].xRot += .5F * crouch;
                hindLower[side].xRot -= .9F * crouch;
            }
            neck.xRot += .3F * crouch;
        }
        if (lunge > 0) {
            for (int side = 0; side < 2; side++) {
                front[side].xRot -= 1.1F * lunge;
                frontLower[side].xRot += .2F * lunge;
                hind[side].xRot += 1.1F * lunge;
                hindLower[side].xRot += .2F * lunge;
            }
            neck.xRot += .45F * lunge;
            head.xRot -= .25F * lunge;
            tail.xRot += 1.2F * lunge;
        }
        // Head lowered (his horn to the ground) or lifted (his horn to the sky).
        neck.xRot += .55F * lower - .45F * lift;
        head.xRot += .35F * lower - .3F * lift;
        jaw.xRot += .5F * mouth;
        horn.xScale = horn.zScale = glow;

        // ---- dying: legs buckling, down on his side ----
        if (dying > 0) {
            float down = ease(dying / .6F);
            body.y += 12 * down;
            body.zRot += 1.25F * ease((dying - .2F) / .6F);
            for (int side = 0; side < 2; side++) {
                front[side].xRot -= .4F * down;
                frontLower[side].xRot += 1.6F * down;
                hind[side].xRot += .5F * down;
                hindLower[side].xRot -= 1.2F * down;
            }
            neck.xRot += .9F * down;
            head.xRot += .4F * down;
        }
    }

    /** The wind-up of the skill he is doing, in ticks, to pose by. */
    static float windup(Skill skill) {
        return switch (skill) {
            case HOOVES -> AmdusiasRules.HOOVES.timing().windup();
            case BLINKS -> AmdusiasRules.BLINKS.timing().windup();
            case SPIKES -> AmdusiasRules.SPIKES.timing().windup();
            case BLIGHT -> AmdusiasRules.BLIGHT.timing().windup();
            case UPHEAVAL -> AmdusiasRules.UPHEAVAL.timing().windup();
            case PSYCHIC -> AmdusiasRules.PSYCHIC.timing().windup();
            case FANFARE -> AmdusiasRules.FANFARE.timing().windup();
            case CRESCENDO -> AmdusiasRules.CRESCENDO.timing().windup();
            case NONE -> 1;
        };
    }

    /** How long the skill he is doing runs, in ticks, to pose by. */
    static float length(Skill skill) {
        return switch (skill) {
            case HOOVES -> AmdusiasRules.HOOVES.timing().ticks();
            case BLINKS -> AmdusiasRules.BLINKS.timing().ticks();
            case SPIKES -> AmdusiasRules.SPIKES.timing().ticks();
            case BLIGHT -> AmdusiasRules.BLIGHT.timing().ticks();
            case UPHEAVAL -> AmdusiasRules.UPHEAVAL.timing().ticks();
            case PSYCHIC -> AmdusiasRules.PSYCHIC.timing().ticks();
            case FANFARE -> AmdusiasRules.FANFARE.timing().ticks();
            case CRESCENDO -> AmdusiasRules.CRESCENDO.timing().ticks();
            case NONE -> 1;
        };
    }
}
