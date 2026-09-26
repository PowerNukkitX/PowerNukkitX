package org.powernukkitx.level.lighting;
import java.util.Arrays;


/**
 * Processes queued block and sky light propagation against a light-access implementation.
 *
 * @author Curse
 */
public final class SubChunkRelighter {
    private static final int MAX_LIGHT = 15;
    private static final int[] OFFSET_X = {-1, 1, 0, 0, 0, 0};
    private static final int[] OFFSET_Y = {0, 0, -1, 1, 0, 0};
    private static final int[] OFFSET_Z = {0, 0, 0, 0, -1, 1};
    private final SubChunkLightAccess access;
    private final LongWorkQueue[] skyRemovalQueues = createQueues();
    private final LongWorkQueue[] skyAdditionQueues = createQueues();
    private final LongWorkQueue[] blockRemovalQueues = createQueues();
    private final LongWorkQueue[] blockAdditionQueues = createQueues();
    /**
     * Creates a new SubChunkRelighter instance.
     *
     * @param access value for this API
     */
    public SubChunkRelighter(SubChunkLightAccess access) {
        this.access = access;
    }

    /**
     * Queues sky light removal.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @param oldLight value for this API
     */
    public void queueSkyRemoval(int x, int y, int z, int oldLight) {
        oldLight = clampLight(oldLight);
        if (oldLight == 0) return;

        skyRemovalQueues[oldLight].enqueue(packPosition(x, y, z));
    }

    /**
     * Queues sky light addition.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     */
    public void queueSkyAddition(int x, int y, int z) {
        if (!access.isAvailable(x, y, z)) return;

        int light = clampLight(access.getSkyLight(x, y, z));
        if (light == 0) return;

        skyAdditionQueues[light].enqueue(packPosition(x, y, z));
    }

    /**
     * Queues block light removal.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @param oldLight value for this API
     */
    public void queueBlockRemoval(int x, int y, int z, int oldLight) {
        oldLight = clampLight(oldLight);
        if (oldLight == 0) return;

        blockRemovalQueues[oldLight].enqueue(packPosition(x, y, z));
    }

    /**
     * Queues block light addition.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     */
    public void queueBlockAddition(int x, int y, int z) {
        if (!access.isAvailable(x, y, z)) return;

        int light = clampLight(access.getBlockLight(x, y, z));
        if (light == 0) return;

        blockAdditionQueues[light].enqueue(packPosition(x, y, z));
    }

    /**
     * Processes queued work.
     */
    public void process() {
        processSkyRemoval();
        processSkyAddition();
        processBlockRemoval();
        processBlockAddition();
    }

    /**
     * Returns whether pending work remains.
     * @return the requested value
     */
    public boolean hasPendingWork() {
        return hasPendingWork(skyRemovalQueues)
                || hasPendingWork(skyAdditionQueues)
                || hasPendingWork(blockRemovalQueues)
                || hasPendingWork(blockAdditionQueues);
    }

    private void processSkyRemoval() {
        for (int light = MAX_LIGHT; light > 0; light--) {
            LongWorkQueue queue = skyRemovalQueues[light];
            while (!queue.isEmpty()) {
                long packed = queue.dequeueLong();
                int x = unpackX(packed);
                int y = unpackY(packed);
                int z = unpackZ(packed);
                removeSkyLightFromNeighbors(x, y, z, light);
            }
        }
    }

    private void removeSkyLightFromNeighbors(int x, int y, int z, int removedLight) {
        for (int direction = 0; direction < 6; direction++) {
            int neighborX = x + OFFSET_X[direction];
            int neighborY = y + OFFSET_Y[direction];
            int neighborZ = z + OFFSET_Z[direction];
            if (!access.isAvailable(neighborX, neighborY, neighborZ)) continue;

            int neighborLight = clampLight(access.getSkyLight(neighborX, neighborY, neighborZ));
            if (neighborLight == 0) continue;

            int attenuation =
                    attenuation(access.getRemovalLightFilter(neighborX, neighborY, neighborZ));
            int propagatedOldLight = removedLight - attenuation;
            if (propagatedOldLight <= 0) continue;

            if (neighborLight <= propagatedOldLight) {
                access.setSkyLight(neighborX, neighborY, neighborZ, 0);
                skyRemovalQueues[neighborLight].enqueue(packPosition(neighborX, neighborY, neighborZ));
            } else {
                skyAdditionQueues[neighborLight].enqueue(packPosition(neighborX, neighborY, neighborZ));
            }
        }
    }

    private void processSkyAddition() {
        for (int light = MAX_LIGHT; light > 0; light--) {
            LongWorkQueue queue = skyAdditionQueues[light];
            while (!queue.isEmpty()) {
                long packed = queue.dequeueLong();
                int x = unpackX(packed);
                int y = unpackY(packed);
                int z = unpackZ(packed);
                if (!access.isAvailable(x, y, z)) continue;

                int currentLight = clampLight(access.getSkyLight(x, y, z));
                if (currentLight != light) continue;

                spreadSkyLight(x, y, z, light);
            }
        }
    }

    private void spreadSkyLight(int x, int y, int z, int sourceLight) {
        for (int direction = 0; direction < 6; direction++) {
            int neighborX = x + OFFSET_X[direction];
            int neighborY = y + OFFSET_Y[direction];
            int neighborZ = z + OFFSET_Z[direction];
            if (!access.isAvailable(neighborX, neighborY, neighborZ)) continue;

            int attenuation = attenuation(access.getLightFilter(neighborX, neighborY, neighborZ));
            int candidateLight = sourceLight - attenuation;
            if (candidateLight <= 0) continue;

            int currentLight = clampLight(access.getSkyLight(neighborX, neighborY, neighborZ));
            if (candidateLight <= currentLight) continue;

            access.setSkyLight(neighborX, neighborY, neighborZ, candidateLight);
            skyAdditionQueues[candidateLight].enqueue(packPosition(neighborX, neighborY, neighborZ));
        }
    }

    private void processBlockRemoval() {
        for (int light = MAX_LIGHT; light > 0; light--) {
            LongWorkQueue queue = blockRemovalQueues[light];
            while (!queue.isEmpty()) {
                long packed = queue.dequeueLong();
                int x = unpackX(packed);
                int y = unpackY(packed);
                int z = unpackZ(packed);
                removeBlockLightFromNeighbors(x, y, z, light);
            }
        }
    }

    private void removeBlockLightFromNeighbors(int x, int y, int z, int removedLight) {
        for (int direction = 0; direction < 6; direction++) {
            int neighborX = x + OFFSET_X[direction];
            int neighborY = y + OFFSET_Y[direction];
            int neighborZ = z + OFFSET_Z[direction];
            if (!access.isAvailable(neighborX, neighborY, neighborZ)) continue;

            int neighborLight = clampLight(access.getBlockLight(neighborX, neighborY, neighborZ));
            if (neighborLight == 0) continue;

            int attenuation = attenuation(access.getRemovalLightFilter(neighborX, neighborY, neighborZ));
            int propagatedOldLight = removedLight - attenuation;
            if (propagatedOldLight <= 0) continue;

            if (neighborLight <= propagatedOldLight) {
                int emission = clampLight(access.getBlockEmission(neighborX, neighborY, neighborZ));
                if (emission >= neighborLight) {
                    if (emission > neighborLight) {
                        access.setBlockLight(neighborX, neighborY, neighborZ, emission);
                    }

                    blockAdditionQueues[emission].enqueue(packPosition(neighborX, neighborY, neighborZ));
                    continue;
                }

                access.setBlockLight(neighborX, neighborY, neighborZ, emission);
                blockRemovalQueues[neighborLight].enqueue(packPosition(neighborX, neighborY, neighborZ));
                if (emission > 0) {
                    blockAdditionQueues[emission].enqueue(packPosition(neighborX, neighborY, neighborZ));
                }
            } else {
                blockAdditionQueues[neighborLight].enqueue(packPosition(neighborX, neighborY, neighborZ));
            }
        }
    }

    private void processBlockAddition() {
        for (int light = MAX_LIGHT; light > 0; light--) {
            LongWorkQueue queue = blockAdditionQueues[light];
            while (!queue.isEmpty()) {
                long packed = queue.dequeueLong();
                int x = unpackX(packed);
                int y = unpackY(packed);
                int z = unpackZ(packed);
                if (!access.isAvailable(x, y, z)) continue;

                int currentLight = clampLight(access.getBlockLight(x, y, z));
                if (currentLight != light) continue;

                spreadBlockLight(x, y, z, light);
            }
        }
    }

    private void spreadBlockLight(int x, int y, int z, int sourceLight) {
        for (int direction = 0; direction < 6; direction++) {
            int neighborX = x + OFFSET_X[direction];
            int neighborY = y + OFFSET_Y[direction];
            int neighborZ = z + OFFSET_Z[direction];
            if (!access.isAvailable(neighborX, neighborY, neighborZ)) continue;

            int attenuation = attenuation(access.getLightFilter(neighborX, neighborY, neighborZ));
            int candidateLight = sourceLight - attenuation;
            int emission = clampLight(access.getBlockEmission(neighborX, neighborY, neighborZ));
            if (emission > candidateLight) {
                candidateLight = emission;
            }

            if (candidateLight <= 0) continue;

            int currentLight = clampLight(access.getBlockLight(neighborX, neighborY, neighborZ));
            if (candidateLight <= currentLight) continue;

            access.setBlockLight(neighborX, neighborY, neighborZ, candidateLight);
            blockAdditionQueues[candidateLight].enqueue(packPosition(neighborX, neighborY, neighborZ));
        }
    }

    private static int attenuation(int lightFilter) {
        if (lightFilter < 1) return 1;
        return Math.min(lightFilter, MAX_LIGHT);
    }

    private static int clampLight(int light) {
        if (light < 0) return 0;
        return Math.min(light, MAX_LIGHT);
    }

    private static boolean hasPendingWork(LongWorkQueue[] queues) {
        for (int light = 1; light <= MAX_LIGHT; light++) {
            if (!queues[light].isEmpty()) return true;
        }
        return false;
    }

    private static LongWorkQueue[] createQueues() {
        LongWorkQueue[] queues = new LongWorkQueue[MAX_LIGHT + 1];
        for (int light = 0; light <= MAX_LIGHT; light++) {
            queues[light] = new LongWorkQueue(128);
        }

        return queues;
    }

    private static long packPosition(int x, int y, int z) {
        return ((long) x & 0x3ffffffL) << 38 | ((long) z & 0x3ffffffL) << 12 | ((long) y & 0xfffL);
    }

    private static int unpackX(long packed) {
        return (int) (packed >> 38);
    }

    private static int unpackY(long packed) {
        int y = (int) (packed & 0xfffL);
        if (y >= 0x800) {
            y -= 0x1000;
        }

        return y;
    }

    private static int unpackZ(long packed) {
        int z = (int) ((packed >> 12) & 0x3ffffffL);
        if (z >= 0x2000000) {
            z -= 0x4000000;
        }

        return z;
    }

    private static final class LongWorkQueue {
        private long[] elements;
        private int readIndex;
        private int writeIndex;
        private LongWorkQueue(int initialCapacity) {
            this.elements = new long[Math.max(1, initialCapacity)];
        }

        private void enqueue(long value) {
            if (readIndex == writeIndex) {
                readIndex = 0;
                writeIndex = 0;
            }

            if (writeIndex >= elements.length) {
                compactOrGrow();
            }

            elements[writeIndex++] = value;
        }

        private long dequeueLong() {
            return elements[readIndex++];
        }

        private boolean isEmpty() {
            return readIndex >= writeIndex;
        }

        private void compactOrGrow() {
            int size = writeIndex - readIndex;
            if (readIndex > 0) {
                System.arraycopy(elements, readIndex, elements, 0, size);
                readIndex = 0;
                writeIndex = size;
                return;
            }

            elements = Arrays.copyOf(elements, elements.length << 1);
        }
    }
}
