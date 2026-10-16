package com.miophas.corruption.common.client.visual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The shapes some effects are drawn along, worked out from a random source alone, so that the same seed gives the
 * same shape every frame: a bolt of lightning's jagged path and its forks, cracks running out over the ground, a
 * spirit's seal, and the lines that join stars into a figure. Plain Java, so they can be checked without a game.
 */
final class VisualPaths {
    /**
     * A jagged path from a to b: halved {@code depth} times over, each new middle pushed aside, across the piece it
     * halves, by up to {@code jag} of that piece's length. Its points, x, y and z one after another; the first is
     * a and the last b.
     */
    static float[] bolt(Random random, float ax, float ay, float az, float bx, float by, float bz, int depth, float jag) {
        int pieces = 1 << Math.max(0, depth);
        float[] points = new float[(pieces + 1) * 3];
        put(points, 0, ax, ay, az);
        put(points, pieces, bx, by, bz);
        for (int step = pieces; step > 1; step /= 2) {
            for (int start = 0; start < pieces; start += step) {
                int end = start + step, middle = start + step / 2;
                float x0 = points[start * 3], y0 = points[start * 3 + 1], z0 = points[start * 3 + 2];
                float dx = points[end * 3] - x0, dy = points[end * 3 + 1] - y0, dz = points[end * 3 + 2] - z0;
                float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                // A random way, less whatever of it lies along the piece: across it.
                float rx = random.nextFloat() * 2 - 1, ry = random.nextFloat() * 2 - 1, rz = random.nextFloat() * 2 - 1;
                if (length > 1e-6F) {
                    float along = (rx * dx + ry * dy + rz * dz) / (length * length);
                    rx -= dx * along;
                    ry -= dy * along;
                    rz -= dz * along;
                }
                float size = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
                float push = size < 1e-6F ? 0 : jag * length * (random.nextFloat() * 2 - 1) / size;
                put(points, middle, x0 + dx / 2 + rx * push, y0 + dy / 2 + ry * push, z0 + dz / 2 + rz * push);
            }
        }
        return points;
    }

    /**
     * Forks off a path, as a bolt of lightning branches: each from one of its points, running on much the way the
     * path runs there but turned aside, up to {@code length} long, jagged as {@link #bolt} makes it.
     */
    static List<float[]> forks(Random random, float[] path, int count, float length, int depth, float jag) {
        List<float[]> forks = new ArrayList<>();
        int points = path.length / 3;
        if (points < 3) return forks;
        for (int fork = 0; fork < count; fork++) {
            int at = 1 + random.nextInt(points - 2);
            float x = path[at * 3], y = path[at * 3 + 1], z = path[at * 3 + 2];
            float dx = path[at * 3 + 3] - x, dy = path[at * 3 + 4] - y, dz = path[at * 3 + 5] - z;
            float run = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (run < 1e-6F) continue;
            dx = dx / run + (random.nextFloat() * 2 - 1) * .9F;
            dy = dy / run + (random.nextFloat() * 2 - 1) * .4F;
            dz = dz / run + (random.nextFloat() * 2 - 1) * .9F;
            float turned = (float) Math.sqrt(dx * dx + dy * dy + dz * dz), reach = length * (.4F + .6F * random.nextFloat()) / turned;
            forks.add(bolt(random, x, y, z, x + dx * reach, y + dy * reach, z + dz * reach, depth, jag));
        }
        return forks;
    }

    /**
     * Cracks running out over the ground from the middle to {@code radius} away, as many arms as asked, each
     * wandering and now and then splitting. Each piece is x0, z0, x1, z1, then how far along the crack its two ends
     * lie from where the crack set out (so that a crack can be drawn running out, piece by piece); every piece lies
     * within the radius.
     */
    static List<float[]> cracks(Random random, int arms, float radius) {
        List<float[]> pieces = new ArrayList<>();
        for (int arm = 0; arm < arms; arm++) {
            float angle = (float) (Math.PI * 2 * (arm + (random.nextFloat() - .5F) * .6F) / arms);
            grow(random, pieces, 0, 0, angle, 0, radius, radius * .14F, 2);
        }
        return pieces;
    }

    private static void grow(Random random, List<float[]> pieces, float x, float z, float angle, float travelled, float limit, float step, int splits) {
        for (int piece = 0; piece < 64; piece++) {
            float reach = (float) Math.sqrt(x * x + z * z);
            if (reach >= limit - 1e-4F) return;
            angle += (random.nextFloat() - .5F) * .9F;
            // Always more out than round: drawn back toward the way straight out.
            if (reach > 1e-3F) angle = turnToward(angle, (float) Math.atan2(z, x), .3F);
            float length = step * (.6F + .8F * random.nextFloat());
            float nx = x + (float) Math.cos(angle) * length, nz = z + (float) Math.sin(angle) * length;
            float out = (float) Math.sqrt(nx * nx + nz * nz);
            if (out > limit) {
                nx *= limit / out;
                nz *= limit / out;
                length = (float) Math.sqrt((nx - x) * (nx - x) + (nz - z) * (nz - z));
            }
            pieces.add(new float[]{x, z, nx, nz, travelled, travelled + length});
            travelled += length;
            x = nx;
            z = nz;
            if (splits > 0 && random.nextFloat() < .3F) {
                float side = random.nextBoolean() ? 1 : -1, branchLimit = reach + (limit - reach) * (.4F + .4F * random.nextFloat());
                grow(random, pieces, x, z, angle + side * (.5F + .5F * random.nextFloat()), travelled, Math.min(limit, branchLimit), step * .8F, splits - 1);
            }
        }
    }

    private static float turnToward(float angle, float toward, float share) {
        float difference = (float) Math.atan2(Math.sin(toward - angle), Math.cos(toward - angle));
        return angle + difference * share;
    }

    /**
     * A spirit's seal, as the grimoires draw one: a stem, bars across it, hooks and arcs off it, strokes out to its
     * edge, and small rings at the ends of some, often the same on either side. Its strokes are lines through
     * points x, y within the unit circle, in the order a hand would trace them; its rings are x, y and radius.
     */
    record Seal(List<float[]> strokes, List<float[]> rings) {}

    static Seal seal(Random random) {
        List<float[]> strokes = new ArrayList<>();
        List<float[]> rings = new ArrayList<>();
        boolean mirrored = random.nextFloat() < .65F;
        float stem = (random.nextFloat() - .5F) * (mirrored ? 0 : .2F), top = .55F + .2F * random.nextFloat(), bottom = -.55F - .2F * random.nextFloat();
        strokes.add(new float[]{stem, bottom, stem, top});
        if (random.nextBoolean()) rings.add(new float[]{stem, top + .08F, .08F});
        int bars = 1 + random.nextInt(3);
        for (int bar = 0; bar < bars; bar++) {
            float y = bottom + (top - bottom) * (bar + .5F + (random.nextFloat() - .5F) * .5F) / bars, half = .25F + .35F * random.nextFloat();
            float tilt = mirrored ? 0 : (random.nextFloat() - .5F) * .3F;
            strokes.add(new float[]{stem - half, y - tilt, stem + half, y + tilt});
            if (random.nextFloat() < .5F) {
                float end = .05F + .04F * random.nextFloat();
                rings.add(new float[]{stem + half + end, y + tilt, end});
                if (mirrored) rings.add(new float[]{stem - half - end, y - tilt, end});
            } else if (random.nextFloat() < .6F) {
                // Ends turned down, or up, as a seal's bars often are.
                float drop = (random.nextBoolean() ? 1 : -1) * (.12F + .12F * random.nextFloat());
                strokes.add(new float[]{stem + half, y + tilt, stem + half, y + tilt + drop});
                strokes.add(new float[]{stem - half, y - tilt, stem - half, y - tilt + drop});
            }
        }
        int arcs = 1 + random.nextInt(2);
        for (int arc = 0; arc < arcs; arc++) {
            float y = bottom + (top - bottom) * random.nextFloat(), size = .15F + .2F * random.nextFloat();
            float facing = (float) (Math.PI / 2 * random.nextInt(4));
            strokes.add(arc(stem, y, size, facing, (float) Math.PI));
        }
        int spokes = random.nextInt(3);
        for (int spoke = 0; spoke < spokes; spoke++) {
            float from = bottom + (top - bottom) * random.nextFloat(), angle = (float) (Math.PI * (.15F + .7F * random.nextFloat()) - Math.PI / 2);
            float x = .62F * (float) Math.cos(angle), y = .62F * (float) Math.sin(angle);
            strokes.add(new float[]{stem, from, stem + x, from + y * .5F});
            if (mirrored) strokes.add(new float[]{stem, from, stem - x, from + y * .5F});
        }
        if (random.nextFloat() < .5F) {
            // A cross at the foot, or a crescent.
            if (random.nextBoolean()) strokes.add(new float[]{stem - .18F, bottom + .1F, stem + .18F, bottom + .1F});
            else strokes.add(arc(stem, bottom, .2F, (float) (Math.PI / 2), (float) Math.PI));
        }
        // Kept inside the circle it is drawn in.
        for (float[] stroke : strokes)
            for (int at = 0; at + 1 < stroke.length; at += 2) {
                float r = (float) Math.sqrt(stroke[at] * stroke[at] + stroke[at + 1] * stroke[at + 1]);
                if (r > .85F) {
                    stroke[at] *= .85F / r;
                    stroke[at + 1] *= .85F / r;
                }
            }
        rings.removeIf(ring -> Math.sqrt(ring[0] * ring[0] + ring[1] * ring[1]) + ring[2] > .9F);
        return new Seal(strokes, rings);
    }

    /** An arc about x, y, this far round, its middle facing this way, this many radians of it, as a line through points. */
    private static float[] arc(float x, float y, float radius, float facing, float span) {
        int points = 9;
        float[] line = new float[points * 2];
        for (int point = 0; point < points; point++) {
            float angle = facing - span / 2 + span * point / (points - 1);
            line[point * 2] = x + (float) Math.cos(angle) * radius;
            line[point * 2 + 1] = y + (float) Math.sin(angle) * radius;
        }
        return line;
    }

    /**
     * Lines joining stars into one figure, as the old star maps join them: each star to the nearest of those
     * already joined, starting from the first, so the figure grows outward from it, star by star. Each line is the
     * two stars' places in the list, the one already joined first. Stars are x, y, z.
     */
    static int[][] figure(float[][] stars) {
        int count = stars.length;
        if (count < 2) return new int[0][];
        int[][] lines = new int[count - 1][];
        boolean[] joined = new boolean[count];
        float[] nearest = new float[count];
        int[] from = new int[count];
        joined[0] = true;
        for (int star = 1; star < count; star++) {
            nearest[star] = distance(stars[0], stars[star]);
            from[star] = 0;
        }
        for (int line = 0; line < count - 1; line++) {
            int next = -1;
            for (int star = 0; star < count; star++)
                if (!joined[star] && (next < 0 || nearest[star] < nearest[next])) next = star;
            joined[next] = true;
            lines[line] = new int[]{from[next], next};
            for (int star = 0; star < count; star++) {
                if (joined[star]) continue;
                float d = distance(stars[next], stars[star]);
                if (d < nearest[star]) {
                    nearest[star] = d;
                    from[star] = next;
                }
            }
        }
        return lines;
    }

    private static float distance(float[] a, float[] b) {
        float dx = a[0] - b[0], dy = a[1] - b[1], dz = a[2] - b[2];
        return dx * dx + dy * dy + dz * dz;
    }

    private static void put(float[] points, int at, float x, float y, float z) {
        points[at * 3] = x;
        points[at * 3 + 1] = y;
        points[at * 3 + 2] = z;
    }

    private VisualPaths() {}
}
