package org.powernukkitx.level;

/**
 * Calculates chunk build priority around a source chunk and view direction. Nearby chunks are preferred and chunks in
 * front of the player receive an additional score boost.
 *
 * @author Curse
 */
public final class ChunkBuildOrderPolicy {
    private static final float DIRECTION_EPSILON = 0.0001f;

    private ChunkBuildOrderPolicy() {
    }

    /**
     * Calculates the priority for a chunk.
     *
     * @param chunkX value for this API
     * @param chunkZ value for this API
     * @param sourceChunkX value for this API
     * @param sourceChunkZ value for this API
     * @param directionX value for this API
     * @param directionYAbs value for this API
     * @param directionZ value for this API
     * @return the requested value
     */
    public static Priority calculate(int chunkX, int chunkZ, int sourceChunkX, int sourceChunkZ, float directionX, float directionYAbs, float directionZ) {
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
     * Represents an ordered chunk build priority result.
     *
     * @param score value for this API
     * @param squaredDistance value for this API
     *
     * @author Curse
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
