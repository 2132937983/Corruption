package com.miophas.corruption.common.client.entity;

import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Keeps larger body motions within the reach of the existing two-joint legs. */
final class RemnantAnimationGrounding {
    static void lowerBody(ModelPart body, Vector3f[] hips, Vector3f[] targets, float[] reaches) {
        Matrix4f inverse = new Matrix4f().rotateZYX(body.zRot, body.yRot, body.xRot)
                .scale(body.xScale, body.yScale, body.zScale).invert();
        Vector3f down = inverse.transformDirection(new Vector3f(0, 1, 0));
        float quadratic = down.lengthSquared(), correction = 0;
        for (int i = 0; i < targets.length; i++) {
            Vector3f local = inverse.transformPosition(new Vector3f(targets[i]).sub(body.x, body.y, body.z)).sub(hips[i]);
            float excess = local.lengthSquared() - reaches[i] * reaches[i];
            if (excess <= 0) continue;
            float projection = local.dot(down), discriminant = projection * projection - quadratic * excess;
            if (projection > 0 && discriminant >= 0)
                correction = Math.max(correction, (projection - (float) Math.sqrt(discriminant)) / quadratic);
        }
        body.y += correction;
    }

    private RemnantAnimationGrounding() {}
}
