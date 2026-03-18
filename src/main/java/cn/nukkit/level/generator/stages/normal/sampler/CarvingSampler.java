package cn.nukkit.level.generator.stages.normal.sampler;

import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoiseSampler;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;

public final class CarvingSampler {

    private static final double CARVING_THRESHOLD = 0.85;
    private static final int CARVING_MIN_HEIGHT = -63;
    private static final int CARVING_MIN_TAPER = 14;
    private static final int CARVING_MAX_HEIGHT = 140;
    private static final int OCEAN_CARVING_MAX_HEIGHT = 40;
    private static final int CARVING_MAX_TAPER = 40;
    private static final double CARVING_CAP = 1.0;
    private static final double CARVING_FLOOR_WARP = 10.0;
    private static final double CARVING_FLOOR_WARP_FREQUENCY = 0.35;
    private static final double CARVING_CEILING_WARP = 18.0;
    private static final double CARVING_CEILING_WARP_FREQUENCY = 0.45;

    private static final double CHEESE_STRENGTH = 0.6;
    private static final double CHEESE_HORIZONTAL_FREQUENCY = 1.5;
    private static final double CHEESE_VERTICAL_FREQUENCY = 3.0;
    private static final int CHEESE_MAX_HEIGHT = 30;
    private static final int CHEESE_MAX_TAPER = 60;
    private static final double CHEESE_CEILING_WARP = 12.0;
    private static final double CHEESE_CEILING_WARP_FREQUENCY = 0.6;

    private static final double SPAGHETTI_STRENGTH_LARGE = 0.89;
    private static final double SPAGHETTI_STRENGTH_SMALL = 0.85;

    private static final double PILLAR_STRENGTH = 0.3;
    private static final double PILLAR_RADIUS = 0.03;

    private static final double MEGA_CAVE_STRENGTH = 0.1;
    private static final double MEGA_CAVE_SIZE = 0.2;

    private static final double SIMPLEX_BASE_FREQUENCY = 0.0075;
    private static final double CELLULAR_FREQUENCY = 0.05;
    private static final double MEGA_CAVE_FREQUENCY = 0.003;

    private final SimplexNoiseSampler simplex2Octave0;
    private final SimplexNoiseSampler simplex2Octave1;
    private final SimplexNoiseSampler simplex2Octave2;
    private final SimplexNoiseSampler simplex2Octave3;
    private final SimplexNoiseSampler simplex3Octave0;
    private final SimplexNoiseSampler simplex3Octave1;
    private final SimplexNoiseSampler megaCaves;
    private final long seed;

    public CarvingSampler(long seed) {
        this.seed = seed;
        this.simplex2Octave0 = new SimplexNoiseSampler(new NukkitRandom(seed ^ 0x2F4A1C3D5B6E7081L));
        this.simplex2Octave1 = new SimplexNoiseSampler(new NukkitRandom(seed ^ 0x13E9B46D9A52F17CL));
        this.simplex2Octave2 = new SimplexNoiseSampler(new NukkitRandom(seed ^ 0x6B7C8D9EAF102345L));
        this.simplex2Octave3 = new SimplexNoiseSampler(new NukkitRandom(seed ^ 0x45D3C2B1A9087766L));
        this.simplex3Octave0 = new SimplexNoiseSampler(new NukkitRandom(seed ^ 0x7C2D95A14EE3B10FL));
        this.simplex3Octave1 = new SimplexNoiseSampler(new NukkitRandom(seed ^ 0x58A73D4CF0129BE6L));
        this.megaCaves = new SimplexNoiseSampler(new NukkitRandom(seed + 777L));
    }

    public boolean shouldCarve(int x, int y, int z, boolean oceanBiome) {
        return sample(x, y, z, oceanBiome ? OCEAN_CARVING_MAX_HEIGHT : CARVING_MAX_HEIGHT) > 0;
    }

    private double sample(int x, int y, int z, int carvingMaxHeight) {
        double warpedCarvingMinHeight = CARVING_MIN_HEIGHT + ceilingWarp(x, z, CARVING_FLOOR_WARP_FREQUENCY, CARVING_FLOOR_WARP);
        double warpedCarvingMaxHeight = carvingMaxHeight + ceilingWarp(x, z, CARVING_CEILING_WARP_FREQUENCY, CARVING_CEILING_WARP);
        double value = -CARVING_THRESHOLD;
        if (y < warpedCarvingMinHeight || y > warpedCarvingMaxHeight) {
            return value;
        }

        double heightMask = smoothMask(y, warpedCarvingMinHeight, warpedCarvingMinHeight + CARVING_MIN_TAPER)
                * smoothMask(y, warpedCarvingMaxHeight, warpedCarvingMaxHeight - CARVING_MAX_TAPER);
        if (heightMask <= 0) {
            return value;
        }

        double spaghetti = Math.max(
                SPAGHETTI_STRENGTH_LARGE * ((-(Math.abs(simplex3(x, y, z)) + Math.abs(simplex3(x, y + 1000, z))) / 2d) + 1d),
                SPAGHETTI_STRENGTH_SMALL * ((-(Math.abs(simplex3(x, y + 2000, z)) + Math.abs(simplex3(x, y + 3000, z))) / 2d) + 1d)
        );

        double carveNoise = spaghetti;
        double warpedCheeseMaxHeight = CHEESE_MAX_HEIGHT + ceilingWarp(x, z, CHEESE_CEILING_WARP_FREQUENCY, CHEESE_CEILING_WARP);
        if (y <= warpedCheeseMaxHeight) {
            double warpedY = (y + simplex2(x, z) * 5d) * CHEESE_VERTICAL_FREQUENCY;
            double cheese = CHEESE_STRENGTH * ((simplex3(
                    x * CHEESE_HORIZONTAL_FREQUENCY,
                    warpedY,
                    z * CHEESE_HORIZONTAL_FREQUENCY
            ) + 1d) / 2d);

            cheese += remap(megaCaves.sample3D(
                    x * MEGA_CAVE_FREQUENCY,
                    y * MEGA_CAVE_FREQUENCY,
                    z * MEGA_CAVE_FREQUENCY
            ), 1d - MEGA_CAVE_SIZE, 0d, 1d, MEGA_CAVE_STRENGTH);

            cheese -= remap(cavePillars(x, z), -1d + PILLAR_RADIUS, 0d, -1d, 1d) * PILLAR_STRENGTH;
            cheese *= smoothMask(y, warpedCheeseMaxHeight, warpedCheeseMaxHeight - CHEESE_MAX_TAPER);
            carveNoise = Math.max(carveNoise, cheese);
        }

        value += Math.min(CARVING_CAP, carveNoise) * heightMask;
        return value;
    }

    private double simplex2(double x, double z) {
        double frequency = SIMPLEX_BASE_FREQUENCY;
        double amplitude = 1d;
        double total = 0d;
        double max = 0d;
        total += simplex2Octave0.sample2D(x * frequency, z * frequency) * amplitude;
        max += amplitude;
        frequency *= 2d;
        amplitude /= 2d;
        total += simplex2Octave1.sample2D(x * frequency, z * frequency) * amplitude;
        max += amplitude;
        frequency *= 2d;
        amplitude /= 2d;
        total += simplex2Octave2.sample2D(x * frequency, z * frequency) * amplitude;
        max += amplitude;
        frequency *= 2d;
        amplitude /= 2d;
        total += simplex2Octave3.sample2D(x * frequency, z * frequency) * amplitude;
        max += amplitude;
        return NukkitMath.clamp(total / max, -0.9d, 0.9d);
    }

    private double simplex3(double x, double y, double z) {
        double frequency = SIMPLEX_BASE_FREQUENCY;
        double amplitude = 1d;
        double total = 0d;
        double max = 0d;
        total += simplex3Octave0.sample3D(x * frequency, y * frequency, z * frequency) * amplitude;
        max += amplitude;
        frequency *= 2d;
        amplitude /= 2d;
        total += simplex3Octave1.sample3D(x * frequency, y * frequency, z * frequency) * amplitude;
        max += amplitude;
        return total / max;
    }

    private double cavePillars(int x, int z) {
        double scaledX = x * CELLULAR_FREQUENCY;
        double scaledZ = z * CELLULAR_FREQUENCY;
        int cellX = NukkitMath.floorDouble(scaledX);
        int cellZ = NukkitMath.floorDouble(scaledZ);
        double nearest = Double.MAX_VALUE;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int sampleCellX = cellX + dx;
                int sampleCellZ = cellZ + dz;
                long hash = mix(sampleCellX, sampleCellZ);
                double featureX = sampleCellX + randomDouble(hash);
                double featureZ = sampleCellZ + randomDouble(hash ^ 0x9E3779B97F4A7C15L);
                double distX = featureX - scaledX;
                double distZ = featureZ - scaledZ;
                nearest = Math.min(nearest, Math.sqrt(distX * distX + distZ * distZ));
            }
        }

        return -NukkitMath.clamp(nearest / 1.41421356237d, 0d, 1d);
    }

    private double ceilingWarp(int x, int z, double frequency, double amplitude) {
        return simplex2(x * frequency, z * frequency) * amplitude;
    }

    private long mix(int x, int z) {
        long hash = seed;
        hash ^= (long) x * 0x632BE59BD9B4E019L;
        hash ^= (long) z * 0x85157AF5D66D3E6BL;
        hash ^= hash >>> 33;
        hash *= 0xFF51AFD7ED558CCDL;
        hash ^= hash >>> 33;
        hash *= 0xC4CEB9FE1A85EC53L;
        hash ^= hash >>> 33;
        return hash;
    }

    private double randomDouble(long value) {
        long bits = (value >>> 11) & ((1L << 53) - 1);
        return bits / (double) (1L << 53);
    }

    private double smoothMask(double value, double from, double to) {
        if (from == to) {
            return value >= from ? 1d : 0d;
        }
        double normalized = NukkitMath.clamp((value - from) / (to - from), 0d, 1d);
        return normalized * normalized * normalized * (normalized * (normalized * 6d - 15d) + 10d);
    }

    private double remap(double value, double inMin, double inMax, double outMin, double outMax) {
        if (inMin == inMax) {
            return outMax;
        }
        return NukkitMath.lerp(value, inMin, outMin, inMax, outMax);
    }
}
