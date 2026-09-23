package org.powernukkitx.level;

import java.util.UUID;

/**
 * Calculates generation priority from directional and region sources.
 *
 * @author Curse
 */
public final class ChunkBuildOrderPolicy {
    private static final float DIRECTION_EPSILON = 0.0001f;

    private ChunkBuildOrderPolicy() {
    }

    /**
     * Calculates directional chunk priority.
     *
     * @param chunkX target chunk X
     * @param chunkZ target chunk Z
     * @param sourceChunkX source chunk X
     * @param sourceChunkZ source chunk Z
     * @param directionX horizontal direction X
     * @param directionYAbs absolute vertical direction
     * @param directionZ horizontal direction Z
     * @return directional priority
     */
    public static Priority calculate(int chunkX, int chunkZ, int sourceChunkX, int sourceChunkZ,
                                     float directionX, float directionYAbs, float directionZ) {
        final int dx = chunkX - sourceChunkX;
        final int dz = chunkZ - sourceChunkZ;
        final long squaredDistance = (long) dx * dx + (long) dz * dz;

        if (squaredDistance == 0) {
            return new Priority(0, 0);
        }

        final float distance = (float) Math.sqrt((float) squaredDistance);
        final float horizontalDirectionLength = (float) Math.sqrt(directionX * directionX + directionZ * directionZ);

        float dot = 0.0f;

        if (horizontalDirectionLength >= DIRECTION_EPSILON && distance >= DIRECTION_EPSILON) {
            final float toChunkX = dx / distance;
            final float toChunkZ = dz / distance;
            final float normalizedDirectionX = directionX / horizontalDirectionLength;
            final float normalizedDirectionZ = directionZ / horizontalDirectionLength;
            dot = toChunkX * normalizedDirectionX + toChunkZ * normalizedDirectionZ;
        }

        float directionFactor = 1.0f;

        if (dot > 0.0f) {
            directionFactor = 0.25f + (1.0f - dot) * 0.75f;
        }

        final float priorityFactor = directionFactor + directionYAbs * (1.0f - directionFactor);

        return new Priority((int) (distance * priorityFactor), squaredDistance);
    }

    /**
     * Creates a transformed generation-priority region.
     *
     * @param registrationId stable source identity
     * @param minX minimum configured chunk X
     * @param minZ minimum configured chunk Z
     * @param maxX maximum configured chunk X
     * @param maxZ maximum configured chunk Z
     * @param circular whether circular scoring is used
     * @param preload whether preload priority is enabled
     * @return transformed region source
     */
    public static RegionSource createRegionSource(UUID registrationId, int minX, int minZ, int maxX, int maxZ,
                                                   boolean circular, boolean preload) {
        SupportBounds bounds = createSupportBounds(minX, minZ, maxX, maxZ);
        return new RegionSource(
                registrationId,
                bounds.minX(),
                bounds.minZ(),
                bounds.maxX() - bounds.minX(),
                bounds.maxZ() - bounds.minZ(),
                circular,
                preload);
    }

    static SupportBounds createSupportBounds(int minX, int minZ, int maxX, int maxZ) {
        final int supportMinX = minX + (minX & 1) - 8;
        final int evenMinZ = minZ & ~1;
        final int supportMinZ = evenMinZ - 6;
        final int supportMaxX = (maxX & ~1) + 8;
        final int supportMaxZ = maxZ + (maxZ & 1) + 6;
        return new SupportBounds(supportMinX, supportMinZ, supportMaxX, supportMaxZ);
    }

    /**
     * Calculates region priority for a chunk.
     *
     * @param chunkX target chunk X
     * @param chunkZ target chunk Z
     * @param source region source
     * @return region score, or {@link Integer#MAX_VALUE} when outside the region
     */
    public static int calculateRegion(int chunkX, int chunkZ, RegionSource source) {
        final float dx = chunkX - source.baseX();
        final float dz = chunkZ - source.baseZ();

        if (dx < 0.0f || dz < 0.0f || dx > source.extentX() || dz > source.extentZ()) {
            return Integer.MAX_VALUE;
        }

        int score = source.preload() ? -1 : 0;
        if (!source.circular()) return score;

        final float half = source.extentX() * 0.5f;
        final float rx = dx - half;
        final float rz = dz - half;

        if (rx * rx + rz * rz > half * half) {
            score++;
        }

        return score;
    }

    /**
     * Represents transformed physical support bounds.
     */
    record SupportBounds(int minX, int minZ, int maxX, int maxZ) {
    }

    /**
     * Represents one region priority source.
     *
     * @param registrationId stable source identity
     * @param baseX transformed base X
     * @param baseZ transformed base Z
     * @param extentX transformed X extent
     * @param extentZ transformed Z extent
     * @param circular whether circular scoring is used
     * @param preload whether preload priority is enabled
     */
    public record RegionSource(
            UUID registrationId,
            int baseX,
            int baseZ,
            float extentX,
            float extentZ,
            boolean circular,
            boolean preload) {
    }

    /**
     * Represents an ordered chunk build priority result.
     *
     * @param score priority score
     * @param squaredDistance squared source distance
     */
    public record Priority(int score, long squaredDistance) implements Comparable<Priority> {

        @Override
        public int compareTo(Priority other) {
            int comparison = Integer.compare(score, other.score);
            if (comparison != 0) return comparison;

            return Long.compare(squaredDistance, other.squaredDistance);
        }
    }
}
