package org.powernukkitx.level.format;

import org.powernukkitx.level.util.NibbleArray;

import java.util.Arrays;
import java.util.concurrent.locks.StampedLock;

/**
 * Stores block and sky light data for one subchunk. It lazily allocates nibble arrays, tracks lighting initialization
 * flags, and caches combined light properties for client updates.
 *
 * @author Curse
 */
public final class SubChunkLightData {
    private static final int SIZE = 16 * 16 * 16;
    private final StampedLock lock = new StampedLock();
    private volatile NibbleArray blockLight;
    private volatile NibbleArray skyLight;
    /*
     * Expanded read-only block-light metadata for this SubChunk.
     *
     * Each byte stores:
     *
     *     bits 0..3 emission
     *     bits 4..7 absorption/filter
     *
     * Relighters can reuse the same 4096-cell representation.
     */
    private volatile byte[] combinedLightProperties;
    private volatile boolean hasMaxSkyLight;
    private volatile boolean needsInitLighting = true;
    private volatile boolean needsClientLighting;
    /**
     * Creates a new SubChunkLightData instance.
     */
    public SubChunkLightData() {}

    /**
     * Creates a new SubChunkLightData instance.
     *
     * @param blockLight value for this API
     * @param skyLight value for this API
     */
    public SubChunkLightData(NibbleArray blockLight, NibbleArray skyLight) {
        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }

    /**
     * Returns block light at the position.
     *
     * @param index value for this API
     * @return the requested value
     */
    public byte getBlockLight(int index) {
        long stamp = lock.tryOptimisticRead();
        NibbleArray storage = blockLight;
        byte value = storage == null ? 0 : storage.get(index);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                storage = blockLight;
                value = storage == null ? 0 : storage.get(index);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return value;
    }

    /**
     * Returns sky light at the position.
     *
     * @param index value for this API
     * @return the requested value
     */
    public byte getSkyLight(int index) {
        long stamp = lock.tryOptimisticRead();
        NibbleArray storage = skyLight;
        boolean maxSkyLight = hasMaxSkyLight;
        byte value = storage == null ? (byte) (maxSkyLight ? 15 : 0) : storage.get(index);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                storage = skyLight;
                value = storage == null ? (byte) (hasMaxSkyLight ? 15 : 0) : storage.get(index);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return value;
    }

    /**
     * Sets block light at the position.
     *
     * @param index value for this API
     * @param value value for this API
     */
    public void setBlockLight(int index, byte value) {
        if (value < 0 || value > 15) {
            throw new IllegalArgumentException("Block light must be between 0 and 15");
        }

        long stamp = lock.writeLock();
        try {
            if (blockLight == null) {
                if (value == 0) return;
                blockLight = new NibbleArray(SIZE);
            }

            blockLight.set(index, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Sets sky light at the position.
     *
     * @param index value for this API
     * @param value value for this API
     */
    public void setSkyLight(int index, byte value) {
        if (value < 0 || value > 15) {
            throw new IllegalArgumentException("Sky light must be between 0 and 15");
        }

        long stamp = lock.writeLock();
        try {
            if (skyLight == null) {
                byte implicitValue = (byte) (hasMaxSkyLight ? 15 : 0);
                if (value == implicitValue) return;

                skyLight = new NibbleArray(SIZE);
                if (implicitValue != 0) skyLight.fill(implicitValue);
                hasMaxSkyLight = false;
            }

            skyLight.set(index, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Fills sky light storage with one value.
     *
     * @param value value for this API
     */
    public void setAllSkyLight(byte value) {
        if (value < 0 || value > 15) {
            throw new IllegalArgumentException("Sky light must be between 0 and 15");
        }

        long stamp = lock.writeLock();
        try {
            /*
             * Keep uniform SKY sections implicit.
             *
             * This matches the native fully-lit / fully-dark representation
             * instead of retaining a 2048-byte nibble array unnecessarily.
             */
            if (value == 15) {
                skyLight = null;
                hasMaxSkyLight = true;
                return;
            }

            if (value == 0) {
                skyLight = null;
                hasMaxSkyLight = false;
                return;
            }

            if (skyLight == null) {
                skyLight = new NibbleArray(SIZE);
            }

            skyLight.fill(value);
            hasMaxSkyLight = false;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Fills block light storage with one value.
     *
     * @param value value for this API
     */
    public void setAllBlockLight(byte value) {
        if (value < 0 || value > 15) {
            throw new IllegalArgumentException("Block light must be between 0 and 15");
        }

        long stamp = lock.writeLock();
        try {
            if (blockLight == null) {
                if (value == 0) return;
                blockLight = new NibbleArray(SIZE);
            }

            blockLight.fill(value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Returns whether sky light is implicitly maximum.
     * @return the requested value
     */
    public boolean hasMaxSkyLight() {
        return skyLight == null && hasMaxSkyLight;
    }

    /**
     * Returns whether sky light storage exists.
     * @return the requested value
     */
    public boolean hasSkyLightStorage() {
        return skyLight != null;
    }

    /**
     * Returns whether block light storage exists.
     * @return the requested value
     */
    public boolean hasBlockLightStorage() {
        return blockLight != null;
    }

    /**
     * Returns whether initial lighting is needed.
     * @return the requested value
     */
    public boolean needsInitLighting() {
        return needsInitLighting;
    }

    /**
     * Sets whether initial lighting is needed.
     *
     * @param needsInitLighting value for this API
     */
    public void setNeedsInitLighting(boolean needsInitLighting) {
        this.needsInitLighting = needsInitLighting;
    }

    /**
     * Returns whether client lighting sync is needed.
     * @return the requested value
     */
    public boolean needsClientLighting() {
        return needsClientLighting;
    }

    /**
     * Sets whether client lighting sync is needed.
     *
     * @param needsClientLighting value for this API
     */
    public void setNeedsClientLighting(boolean needsClientLighting) {
        this.needsClientLighting = needsClientLighting;
    }

    /**
     * Returns block light storage, creating it if needed.
     * @return the requested value
     */
    public NibbleArray getOrCreateBlockLightStorage() {
        long stamp = lock.writeLock();
        try {
            if (blockLight == null) blockLight = new NibbleArray(SIZE);
            return blockLight;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Returns sky light storage, creating it if needed.
     * @return the requested value
     */
    public NibbleArray getOrCreateSkyLightStorage() {
        long stamp = lock.writeLock();
        try {
            if (skyLight == null) {
                skyLight = new NibbleArray(SIZE);
                if (hasMaxSkyLight) skyLight.fill((byte) 15);
                hasMaxSkyLight = false;
            }

            return skyLight;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Copies block light data to the target array.
     *
     * @param target value for this API
     */
    public void copyBlockLightTo(byte[] target) {
        checkExpandedLightArray(target);
        long stamp = lock.readLock();
        try {
            if (blockLight == null) {
                Arrays.fill(target, (byte) 0);
                return;
            }

            expand(blockLight, target);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * Copies sky light data to the target array.
     *
     * @param target value for this API
     */
    public void copySkyLightTo(byte[] target) {
        checkExpandedLightArray(target);
        long stamp = lock.readLock();
        try {
            if (skyLight == null) {
                Arrays.fill(target, (byte) (hasMaxSkyLight ? 15 : 0));
                return;
            }

            expand(skyLight, target);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * Replaces block light data from the source array.
     *
     * @param source value for this API
     */
    public void replaceBlockLightFrom(byte[] source) {
        checkExpandedLightArray(source);
        boolean allZero = true;
        for (byte value : source) {
            if (value != 0) {
                allZero = false;
                break;
            }
        }

        long stamp = lock.writeLock();
        try {
            if (allZero) {
                blockLight = null;
                return;
            }

            blockLight = new NibbleArray(pack(source));
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Replaces sky light data from the source array.
     *
     * @param source value for this API
     */
    public void replaceSkyLightFrom(byte[] source) {
        checkExpandedLightArray(source);
        boolean allZero = true;
        boolean allMax = true;
        for (byte value : source) {
            if (value != 0) allZero = false;
            if (value != 15) allMax = false;
            if (!allZero && !allMax) break;
        }

        long stamp = lock.writeLock();
        try {
            if (allMax) {
                skyLight = null;
                hasMaxSkyLight = true;
                return;
            }

            if (allZero) {
                skyLight = null;
                hasMaxSkyLight = false;
                return;
            }

            skyLight = new NibbleArray(pack(source));
            hasMaxSkyLight = false;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Returns the combined light properties cache.
     * @return the requested value
     */
    public byte[] getCombinedLightPropertiesCache() {
        return combinedLightProperties;
    }

    /**
     * Installs a combined light properties cache.
     *
     * @param cache value for this API
     * @return the requested value
     */
    public byte[] installCombinedLightPropertiesCache(byte[] cache) {
        if (cache == null || cache.length != SIZE) {
            throw new IllegalArgumentException("Combined light property cache must contain exactly " + SIZE + " cells");
        }

        byte[] current = combinedLightProperties;
        if (current != null) return current;

        synchronized (this) {
            current = combinedLightProperties;
            if (current == null) {
                combinedLightProperties = cache;
                current = cache;
            }
        }

        return current;
    }

    /**
     * Updates one combined light properties cache entry.
     *
     * @param index value for this API
     * @param value value for this API
     */
    public void updateCombinedLightPropertiesCache(int index, byte value) {
        byte[] cache = combinedLightProperties;
        if (cache == null) return;
        cache[index] = value;
    }

    private static void checkExpandedLightArray(byte[] array) {
        if (array == null || array.length != SIZE) {
            throw new IllegalArgumentException("Light array must contain exactly " + SIZE + " cells");
        }
    }

    private static void expand(NibbleArray source, byte[] target) {
        byte[] raw = source.getData();
        for (int rawIndex = 0; rawIndex < raw.length; rawIndex++) {
            int packed = raw[rawIndex] & 0xff;
            int targetIndex = rawIndex << 1;
            target[targetIndex] = (byte) (packed & 0x0f);
            target[targetIndex + 1] = (byte) (packed >>> 4);
        }
    }

    private static byte[] pack(byte[] source) {
        byte[] result = new byte[SIZE >> 1];
        for (int index = 0; index < result.length; index++) {
            int sourceIndex = index << 1;
            int low = source[sourceIndex] & 0x0f;
            int high = source[sourceIndex + 1] & 0x0f;
            result[index] = (byte) (low | high << 4);
        }

        return result;
    }
}
