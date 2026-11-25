package org.powernukkitx.level.lighting;

import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Checks {@link SubChunkRelighter} against a brute-force fixed point of the same propagation rule:
 * a cell's light is the larger of its own source and the brightest neighbour minus the cell's
 * attenuation. Light additions must settle on that answer.
 */
class SubChunkRelighterTest {
    private static final int SIZE_X = 20;
    private static final int SIZE_Y = 24;
    private static final int SIZE_Z = 20;
    private static final int[][] DIRECTIONS = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};

    @Test
    void blockLightAdditionsMatchReference() {
        for (int seed = 0; seed < 40; seed++) {
            SplittableRandom random = new SplittableRandom(seed);
            GridAccess access = GridAccess.random(random);
            SubChunkRelighter relighter = new SubChunkRelighter(access);

            for (int i = 0; i < access.emission.length; i++) {
                if (access.emission[i] > 0) {
                    access.block[i] = access.emission[i];
                    relighter.queueBlockAddition(x(i), y(i), z(i));
                }
            }
            relighter.process();
            assertArrayEquals(reference(access, access.emission), access.block, "placing sources, seed " + seed);
        }
    }

    @Test
    void skyLightAdditionsMatchReference() {
        for (int seed = 0; seed < 40; seed++) {
            SplittableRandom random = new SplittableRandom(1000 + seed);
            GridAccess access = GridAccess.random(random);
            SubChunkRelighter relighter = new SubChunkRelighter(access);
            byte[] sources = new byte[access.sky.length];

            // Seed the top layer as open sky, like a section boundary facing a fully lit section.
            for (int x = 0; x < SIZE_X; x++) {
                for (int z = 0; z < SIZE_Z; z++) {
                    int i = index(x, SIZE_Y - 1, z);
                    sources[i] = 15;
                    access.sky[i] = 15;
                    relighter.queueSkyAddition(x, SIZE_Y - 1, z);
                }
            }
            relighter.process();
            assertArrayEquals(reference(access, sources), access.sky, "sky flood, seed " + seed);
        }
    }

    private static byte[] reference(GridAccess access, byte[] sources) {
        byte[] light = sources.clone();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < light.length; i++) {
                int attenuation = Math.max(1, Math.min(access.filter[i], 15));
                int best = sources[i];
                for (int[] d : DIRECTIONS) {
                    int nx = x(i) + d[0], ny = y(i) + d[1], nz = z(i) + d[2];
                    if (!inside(nx, ny, nz)) continue;
                    best = Math.max(best, light[index(nx, ny, nz)] - attenuation);
                }
                if (best != light[i]) {
                    light[i] = (byte) best;
                    changed = true;
                }
            }
        }
        return light;
    }

    private static boolean inside(int x, int y, int z) {
        return x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y && z >= 0 && z < SIZE_Z;
    }

    private static int index(int x, int y, int z) {
        return (x * SIZE_Z + z) * SIZE_Y + y;
    }

    private static int x(int index) {
        return index / (SIZE_Z * SIZE_Y);
    }

    private static int y(int index) {
        return index % SIZE_Y;
    }

    private static int z(int index) {
        return (index / SIZE_Y) % SIZE_Z;
    }

    private static final class GridAccess implements SubChunkLightAccess {
        private final byte[] filter = new byte[SIZE_X * SIZE_Y * SIZE_Z];
        private final byte[] emission = new byte[filter.length];
        private final byte[] block = new byte[filter.length];
        private final byte[] sky = new byte[filter.length];

        static GridAccess random(SplittableRandom random) {
            GridAccess access = new GridAccess();
            for (int i = 0; i < access.filter.length; i++) {
                int roll = random.nextInt(100);
                access.filter[i] = (byte) (roll < 60 ? 0 : roll < 75 ? 1 + random.nextInt(3) : 15);
                if (access.filter[i] < 15 && random.nextInt(150) == 0) {
                    access.emission[i] = (byte) (1 + random.nextInt(15));
                }
            }
            return access;
        }

        @Override
        public boolean isAvailable(int x, int y, int z) {
            return inside(x, y, z);
        }

        @Override
        public int getLightFilter(int x, int y, int z) {
            return filter[index(x, y, z)];
        }

        @Override
        public int getBlockEmission(int x, int y, int z) {
            return emission[index(x, y, z)];
        }

        @Override
        public int getBlockLight(int x, int y, int z) {
            return block[index(x, y, z)];
        }

        @Override
        public void setBlockLight(int x, int y, int z, int light) {
            block[index(x, y, z)] = (byte) light;
        }

        @Override
        public int getSkyLight(int x, int y, int z) {
            return sky[index(x, y, z)];
        }

        @Override
        public void setSkyLight(int x, int y, int z, int light) {
            sky[index(x, y, z)] = (byte) light;
        }
    }
}
