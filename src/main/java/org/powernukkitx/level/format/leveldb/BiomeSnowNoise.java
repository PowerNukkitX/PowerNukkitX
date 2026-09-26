package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.level.biome.BiomeID;

/**
 * Fixed simplex noise used by biome snow reconciliation.
 *
 * @author Curse
 */
final class BiomeSnowNoise {
    private static final float F2 = 0.5f * ((float) Math.sqrt(3.0) - 1.0f);
    private static final float G2 = (3.0f - (float) Math.sqrt(3.0)) / 6.0f;
    private static final float F3 = 1.0f / 3.0f;
    private static final float G3 = 1.0f / 6.0f;
    private static final int[][] GRADIENTS = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };

    private static final Simplex[] SNOW_DEPTH = createSamplers(89328, 5);
    private static final Simplex[] FROZEN = createSamplers(3456, 3);
    private static final Simplex FROZEN_DETAIL = createSamplers(2345, 1)[0];
    private static final Simplex TEMPERATURE = createSamplers(1234, 1)[0];

    private BiomeSnowNoise() {
    }

    static float snowDepthFactor(int x, int y, int z) {
        float scale = 1.0f;
        float sum = 0.0f;

        for (Simplex simplex : SNOW_DEPTH) {
            sum += simplex.sample3D(x * scale, y * scale, z * scale) / scale;
            scale *= 0.5f;
        }

        return (sum + 26.0f) / 52.0f - 0.17f;
    }

    static float positionTemperature(int biomeId, float baseTemperature, int x, int y, int z) {
        float temperature = baseTemperature;

        if (isPartiallyFrozen(biomeId)) {
            float frozenNoise = octave2D(FROZEN, x * 0.05f, z * 0.05f) + FROZEN_DETAIL.sample2D(x * 0.2f, z * 0.2f);
            if (frozenNoise < 0.3f && FROZEN_DETAIL.sample2D(x * 0.09f, z * 0.09f) < 0.8f) {
                temperature = 0.2f;
            }
        }

        if (y > 64) {
            byte quantized = (byte) (int) (TEMPERATURE.sample2D(x * 0.125f, z * 0.125f) * 64.0f);
            float noiseOffset = quantized * 0.125f;
            temperature += (y + noiseOffset - 64.0f) * -0.05f / 40.0f;
        }

        return temperature;
    }

    private static boolean isPartiallyFrozen(int biomeId) {
        return biomeId == BiomeID.FROZEN_OCEAN || biomeId == BiomeID.DEEP_FROZEN_OCEAN;
    }

    private static float octave2D(Simplex[] samplers, float x, float z) {
        float scale = 1.0f;
        float sum = 0.0f;

        for (Simplex sampler : samplers) {
            sum += sampler.sample2D(x * scale, z * scale) / scale;
            scale *= 0.5f;
        }

        return sum;
    }

    private static Simplex[] createSamplers(int seed, int count) {
        Mt19937 random = new Mt19937(seed);
        Simplex[] samplers = new Simplex[count];
        for (int i = 0; i < count; i++) {
            samplers[i] = new Simplex(random);
        }
        return samplers;
    }

    private static int fastFloor(float value) {
        return value > 0.0f ? (int) value : (int) value - 1;
    }

    private static final class Simplex {
        private final int[] permutations = new int[512];

        private Simplex(Mt19937 random) {
            random.nextInt();
            random.nextInt();
            random.nextInt();

            for (int i = 0; i < 256; i++) {
                permutations[i] = i;
            }

            for (int i = 0; i < 256; i++) {
                int j = i + (int) (Integer.toUnsignedLong(random.nextInt()) % (256 - i));
                int value = permutations[i];
                permutations[i] = permutations[j];
                permutations[j] = value;
                permutations[i + 256] = permutations[i];
            }
        }

        private float sample2D(float x, float z) {
            float skew = (x + z) * F2;
            int i = fastFloor(x + skew);
            int j = fastFloor(z + skew);
            float unskew = (i + j) * G2;
            float x0 = x - (i - unskew);
            float z0 = z - (j - unskew);

            int i1;
            int j1;
            if (x0 > z0) {
                i1 = 1;
                j1 = 0;
            } else {
                i1 = 0;
                j1 = 1;
            }

            float x1 = x0 - i1 + G2;
            float z1 = z0 - j1 + G2;
            float x2 = x0 - 1.0f + 2.0f * G2;
            float z2 = z0 - 1.0f + 2.0f * G2;

            int ii = i & 255;
            int jj = j & 255;

            float result = corner2D(permutations[ii + permutations[jj]] % 12, x0, z0);
            result += corner2D(permutations[ii + i1 + permutations[jj + j1]] % 12, x1, z1);
            result += corner2D(permutations[ii + 1 + permutations[jj + 1]] % 12, x2, z2);
            return result * 70.0f;
        }

        private float sample3D(float x, float y, float z) {
            float skew = (x + y + z) * F3;
            int i = fastFloor(x + skew);
            int j = fastFloor(y + skew);
            int k = fastFloor(z + skew);
            float unskew = (i + j + k) * G3;
            float x0 = x - (i - unskew);
            float y0 = y - (j - unskew);
            float z0 = z - (k - unskew);

            int i1;
            int j1;
            int k1;
            int i2;
            int j2;
            int k2;

            if (x0 >= y0) {
                if (y0 >= z0) {
                    i1 = 1;
                    j1 = 0;
                    k1 = 0;
                    i2 = 1;
                    j2 = 1;
                    k2 = 0;
                } else if (x0 >= z0) {
                    i1 = 1;
                    j1 = 0;
                    k1 = 0;
                    i2 = 1;
                    j2 = 0;
                    k2 = 1;
                } else {
                    i1 = 0;
                    j1 = 0;
                    k1 = 1;
                    i2 = 1;
                    j2 = 0;
                    k2 = 1;
                }
            } else if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }

            float x1 = x0 - i1 + G3;
            float y1 = y0 - j1 + G3;
            float z1 = z0 - k1 + G3;
            float x2 = x0 - i2 + 2.0f * G3;
            float y2 = y0 - j2 + 2.0f * G3;
            float z2 = z0 - k2 + 2.0f * G3;
            float x3 = x0 - 1.0f + 3.0f * G3;
            float y3 = y0 - 1.0f + 3.0f * G3;
            float z3 = z0 - 1.0f + 3.0f * G3;

            int ii = i & 255;
            int jj = j & 255;
            int kk = k & 255;

            float result = corner3D(permutations[ii + permutations[jj + permutations[kk]]] % 12, x0, y0, z0);
            result += corner3D(permutations[ii + i1 + permutations[jj + j1 + permutations[kk + k1]]] % 12, x1, y1, z1);
            result += corner3D(permutations[ii + i2 + permutations[jj + j2 + permutations[kk + k2]]] % 12, x2, y2, z2);
            result += corner3D(permutations[ii + 1 + permutations[jj + 1 + permutations[kk + 1]]] % 12, x3, y3, z3);
            return result * 32.0f;
        }

        private float corner2D(int gradient, float x, float z) {
            float attenuation = 0.5f - x * x - z * z;
            if (attenuation < 0.0f) return 0.0f;
            attenuation *= attenuation;
            int[] g = GRADIENTS[gradient];
            return attenuation * attenuation * (g[0] * x + g[1] * z);
        }

        private float corner3D(int gradient, float x, float y, float z) {
            float attenuation = 0.6f - x * x - y * y - z * z;
            if (attenuation < 0.0f) return 0.0f;
            attenuation *= attenuation;
            int[] g = GRADIENTS[gradient];
            return attenuation * attenuation * (g[0] * x + g[1] * y + g[2] * z);
        }
    }

    private static final class Mt19937 {
        private static final int SIZE = 624;
        private static final int PERIOD = 397;
        private final int[] state = new int[SIZE];
        private int index = SIZE;

        private Mt19937(int seed) {
            state[0] = seed;
            for (int i = 1; i < SIZE; i++) {
                state[i] = 1812433253 * (state[i - 1] ^ state[i - 1] >>> 30) + i;
            }
        }

        private int nextInt() {
            if (index >= SIZE) twist();

            int value = state[index++];
            value ^= value >>> 11;
            value ^= value << 7 & 0x9d2c5680;
            value ^= value << 15 & 0xefc60000;
            value ^= value >>> 18;
            return value;
        }

        private void twist() {
            for (int i = 0; i < SIZE; i++) {
                int value = state[i] & 0x80000000 | state[(i + 1) % SIZE] & 0x7fffffff;
                state[i] = state[(i + PERIOD) % SIZE] ^ value >>> 1;
                if ((value & 1) != 0) state[i] ^= 0x9908b0df;
            }
            index = 0;
        }
    }
}
