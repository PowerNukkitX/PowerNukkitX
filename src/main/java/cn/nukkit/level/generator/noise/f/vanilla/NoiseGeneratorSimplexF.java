package cn.nukkit.level.generator.noise.f.vanilla;

import cn.nukkit.utils.random.NukkitRandom;

public class NoiseGeneratorSimplexF {
    public static final float SQRT_3 = (float) Math.sqrt(3.0);
    public static final float SQRT_5 = (float) Math.sqrt(5.0);

    private static final int[][] grad3 = new int[][]{
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    // Skewing and unskewing factors for 2D and 3D
    private static final float F3 = 1.0f / 3.0f;
    private static final float G3 = 1.0f / 6.0f;

    private final int[] p;
    public float xo;
    public float yo;
    public float zo;

    public NoiseGeneratorSimplexF() {
        this(new NukkitRandom(System.currentTimeMillis()));
    }

    public NoiseGeneratorSimplexF(NukkitRandom random) {
        this.p = new int[512];
        this.xo = random.nextFloat() * 256.0f;
        this.yo = random.nextFloat() * 256.0f;
        this.zo = random.nextFloat() * 256.0f;

        int i = 0;
        while (i < 256) {
            this.p[i] = i++;
        }

        for (int l = 0; l < 256; ++l) {
            int j = random.nextBoundedInt(256 - l) + l;
            int k = this.p[l];
            this.p[l] = this.p[j];
            this.p[j] = k;
            this.p[l + 256] = this.p[l];
        }
    }

    private static int fastFloor(float value) {
        return value > 0.0f ? (int) value : (int) value - 1;
    }

    private static float dot(int[] g, float x, float y, float z) {
        return g[0] * x + g[1] * y + g[2] * z;
    }

    /** 3D Simplex Noise */
    public float getValue(float xin, float yin, float zin) {
        float n0, n1, n2, n3;

        // Skew input space
        float s = (xin + yin + zin) * F3;
        int i = fastFloor(xin + s);
        int j = fastFloor(yin + s);
        int k = fastFloor(zin + s);

        float t = (i + j + k) * G3;
        float X0 = i - t;
        float Y0 = j - t;
        float Z0 = k - t;

        float x0 = xin - X0;
        float y0 = yin - Y0;
        float z0 = zin - Z0;

        int i1, j1, k1;
        int i2, j2, k2;

        if (x0 >= y0) {
            if (y0 >= z0) { i1=1; j1=0; k1=0; i2=1; j2=1; k2=0; }
            else if (x0 >= z0) { i1=1; j1=0; k1=0; i2=1; j2=0; k2=1; }
            else { i1=0; j1=0; k1=1; i2=1; j2=0; k2=1; }
        } else {
            if (y0 < z0) { i1=0; j1=0; k1=1; i2=0; j2=1; k2=1; }
            else if (x0 < z0) { i1=0; j1=1; k1=0; i2=0; j2=1; k2=1; }
            else { i1=0; j1=1; k1=0; i2=1; j2=1; k2=0; }
        }

        float x1 = x0 - i1 + G3;
        float y1 = y0 - j1 + G3;
        float z1 = z0 - k1 + G3;
        float x2 = x0 - i2 + 2.0f*G3;
        float y2 = y0 - j2 + 2.0f*G3;
        float z2 = z0 - k2 + 2.0f*G3;
        float x3 = x0 - 1.0f + 3.0f*G3;
        float y3 = y0 - 1.0f + 3.0f*G3;
        float z3 = z0 - 1.0f + 3.0f*G3;

        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;

        int gi0 = p[ii + p[jj + p[kk]]] % 12;
        int gi1 = p[ii + i1 + p[jj + j1 + p[kk + k1]]] % 12;
        int gi2 = p[ii + i2 + p[jj + j2 + p[kk + k2]]] % 12;
        int gi3 = p[ii + 1 + p[jj + 1 + p[kk + 1]]] % 12;

        float t0 = 0.6f - x0*x0 - y0*y0 - z0*z0;
        if (t0 < 0) n0 = 0.0f;
        else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0, z0);
        }

        float t1 = 0.6f - x1*x1 - y1*y1 - z1*z1;
        if (t1 < 0) n1 = 0.0f;
        else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1, z1);
        }

        float t2 = 0.6f - x2*x2 - y2*y2 - z2*z2;
        if (t2 < 0) n2 = 0.0f;
        else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2, z2);
        }

        float t3 = 0.6f - x3*x3 - y3*y3 - z3*z3;
        if (t3 < 0) n3 = 0.0f;
        else {
            t3 *= t3;
            n3 = t3 * t3 * dot(grad3[gi3], x3, y3, z3);
        }

        return 32.0f * (n0 + n1 + n2 + n3); // scale factor
    }

    /** Beispiel für Batch 3D Noise in Array schreiben */
    public void add(float[] buffer, float x, float y, float z,
                    int sizeX, int sizeY, int sizeZ,
                    float scaleX, float scaleY, float scaleZ,
                    float amplitude) {
        int idx = 0;
        for (int dz = 0; dz < sizeZ; dz++) {
            float zz = (z + dz) * scaleZ + this.zo;
            for (int dy = 0; dy < sizeY; dy++) {
                float yy = (y + dy) * scaleY + this.yo;
                for (int dx = 0; dx < sizeX; dx++) {
                    float xx = (x + dx) * scaleX + this.xo;
                    buffer[idx++] += getValue(xx, yy, zz) * amplitude;
                }
            }
        }
    }
}
