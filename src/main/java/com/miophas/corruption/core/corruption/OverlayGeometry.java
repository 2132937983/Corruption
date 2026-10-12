package com.miophas.corruption.core.corruption;

/** Geometry math shared by overlay baking; no client classes or texture I/O. */
public final class OverlayGeometry {
    // Minecraft model pixels are 1/16 of a block: 0.01 pixels = 0.000625 blocks.
    public static final float OFFSET = 0.01F / 16.0F;

    /** Unit surface normal from the quad's actual geometry, oriented toward the declared face. */
    public static float[] faceNormal(float[] positions, float fallbackX, float fallbackY, float fallbackZ) {
        float ax = positions[3] - positions[0], ay = positions[4] - positions[1], az = positions[5] - positions[2];
        float bx = positions[6] - positions[0], by = positions[7] - positions[1], bz = positions[8] - positions[2];
        float nx = ay * bz - az * by, ny = az * bx - ax * bz, nz = ax * by - ay * bx;
        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 0.000001F) {
            ax = positions[9] - positions[0]; ay = positions[10] - positions[1]; az = positions[11] - positions[2];
            nx = by * az - bz * ay; ny = bz * ax - bx * az; nz = bx * ay - by * ax;
            length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        }
        if (length < 0.000001F) return new float[] {fallbackX, fallbackY, fallbackZ};
        float sign = nx * fallbackX + ny * fallbackY + nz * fallbackZ < 0 ? -1 : 1;
        return new float[] {nx * sign / length, ny * sign / length, nz * sign / length};
    }

    /** Geometry key independent of winding/start vertex; normalize signed zero too. */
    public static int[] canonicalPositions(float[] positions) {
        int[] result = new int[12];
        for (int i = 0; i < 12; i++) result[i] = positions[i] == 0 ? 0 : Float.floatToIntBits(positions[i]);
        for (int vertex = 1; vertex < 4; vertex++) {
            int x = result[vertex * 3], y = result[vertex * 3 + 1], z = result[vertex * 3 + 2];
            int at = vertex;
            while (at > 0 && compare(result, (at - 1) * 3, x, y, z) > 0) {
                System.arraycopy(result, (at - 1) * 3, result, at * 3, 3);
                at--;
            }
            result[at * 3] = x; result[at * 3 + 1] = y; result[at * 3 + 2] = z;
        }
        return result;
    }

    private static int compare(int[] positions, int at, int x, int y, int z) {
        int order = Integer.compare(positions[at], x);
        if (order == 0) order = Integer.compare(positions[at + 1], y);
        return order == 0 ? Integer.compare(positions[at + 2], z) : order;
    }

    public static float remapUv(float value, float sourceMin, float sourceMax, float overlayMin, float overlayMax) {
        float normalized = sourceMax == sourceMin ? 0 : (value - sourceMin) / (sourceMax - sourceMin);
        return overlayMin + normalized * (overlayMax - overlayMin);
    }

    private OverlayGeometry() {}
}
