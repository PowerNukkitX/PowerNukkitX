package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Encodes and decodes Bedrock LevelDB actor storage keys and actor digests. It maps ActorUniqueID values to storage IDs
 * and serializes per-chunk actor references.
 *
 * @author Curse
 */
public final class LevelDBActorStorage {
    private static final byte[] ACTOR_PREFIX = "actorprefix".getBytes(StandardCharsets.UTF_8);
    private static final byte[] DIGEST_PREFIX = "digp".getBytes(StandardCharsets.UTF_8);
    private LevelDBActorStorage() {}

    static byte[] getActorPrefix() {
        return ACTOR_PREFIX.clone();
    }

    static byte[] getDigestPrefix() {
        return DIGEST_PREFIX.clone();
    }

    static long getActorStorageKeyFromKey(byte[] key) {
        if (key.length != ACTOR_PREFIX.length + Long.BYTES) {
            throw new IllegalArgumentException("Invalid actorprefix key length: " + key.length);
        }

        for (int i = 0; i < ACTOR_PREFIX.length; i++) {
            if (key[i] != ACTOR_PREFIX[i]) {
                throw new IllegalArgumentException("Invalid actorprefix key");
            }
        }

        return readLongBE(key, ACTOR_PREFIX.length);
    }

    /**
     * Returns the storage key for an actor unique ID.
     *
     * @param actorUniqueId value for this API
     * @return the requested value
     */
    public static long getActorStorageKey(long actorUniqueId) {
        long signedHigh = actorUniqueId >> 32;
        if (signedHigh >= 0) {
            throw new IllegalArgumentException("Actor UniqueID must have a negative high 32-bit component: " + actorUniqueId);
        }

        long storageHigh = -signedHigh;
        long storageLow = actorUniqueId & 0xffffffffL;
        return (storageHigh << 32) | storageLow;
    }

    /**
     * Returns the actor unique ID for a storage key.
     *
     * @param actorStorageKey value for this API
     * @return the requested value
     */
    public static long getActorUniqueId(long actorStorageKey) {
        long storageHigh = (actorStorageKey >>> 32) & 0xffffffffL;
        long storageLow = actorStorageKey & 0xffffffffL;
        long signedHigh = -storageHigh;
        return (signedHigh << 32) | storageLow;
    }

    /**
     * Returns the LevelDB actor key.
     *
     * @param actorStorageKey value for this API
     * @return the requested value
     */
    public static byte[] getActorKey(long actorStorageKey) {
        byte[] key = new byte[ACTOR_PREFIX.length + Long.BYTES];
        System.arraycopy(ACTOR_PREFIX, 0, key, 0, ACTOR_PREFIX.length);
        writeLongBE(key, ACTOR_PREFIX.length, actorStorageKey);
        return key;
    }

    /**
     * Returns the LevelDB actor digest key.
     *
     * @param chunkX value for this API
     * @param chunkZ value for this API
     * @param dimensionData value for this API
     * @return the requested value
     */
    public static byte[] getDigestKey(int chunkX, int chunkZ, DimensionData dimensionData) {
        boolean overworld = dimensionData.getDimensionId() == Level.DIMENSION_OVERWORLD;
        int length =
                DIGEST_PREFIX.length
                        + Integer.BYTES
                        + Integer.BYTES
                        + (overworld ? 0 : Integer.BYTES);
        byte[] key = new byte[length];
        int offset = 0;
        System.arraycopy(DIGEST_PREFIX, 0, key, offset, DIGEST_PREFIX.length);
        offset += DIGEST_PREFIX.length;
        writeIntLE(key, offset, chunkX);
        offset += Integer.BYTES;
        writeIntLE(key, offset, chunkZ);
        offset += Integer.BYTES;
        if (!overworld) {
            writeIntLE(key, offset, dimensionData.getDimensionId());
        }

        return key;
    }

    /**
     * Serializes actor storage keys into a digest.
     *
     * @param actorStorageKeys value for this API
     * @return the requested value
     */
    public static byte[] writeDigest(List<Long> actorStorageKeys) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(actorStorageKeys.size() * Long.BYTES);
        for (long actorStorageKey : actorStorageKeys) {
            byte[] value = new byte[Long.BYTES];
            writeLongBE(value, 0, actorStorageKey);
            outputStream.writeBytes(value);
        }

        return outputStream.toByteArray();
    }

    /**
     * Reads actor storage keys from a digest.
     *
     * @param data value for this API
     * @return the requested value
     */
    public static List<Long> readDigest(byte[] data) {
        if ((data.length % Long.BYTES) != 0) {
            throw new IllegalArgumentException("Actor digest length must be divisible by 8, got " + data.length);
        }

        List<Long> result = new ArrayList<>(data.length / Long.BYTES);
        for (int offset = 0; offset < data.length; offset += Long.BYTES) {
            result.add(readLongBE(data, offset));
        }

        return result;
    }

    private static void writeIntLE(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xff);
        data[offset + 1] = (byte) ((value >>> 8) & 0xff);
        data[offset + 2] = (byte) ((value >>> 16) & 0xff);
        data[offset + 3] = (byte) ((value >>> 24) & 0xff);
    }

    private static void writeLongBE(byte[] data, int offset, long value) {
        data[offset] = (byte) ((value >>> 56) & 0xff);
        data[offset + 1] = (byte) ((value >>> 48) & 0xff);
        data[offset + 2] = (byte) ((value >>> 40) & 0xff);
        data[offset + 3] = (byte) ((value >>> 32) & 0xff);
        data[offset + 4] = (byte) ((value >>> 24) & 0xff);
        data[offset + 5] = (byte) ((value >>> 16) & 0xff);
        data[offset + 6] = (byte) ((value >>> 8) & 0xff);
        data[offset + 7] = (byte) (value & 0xff);
    }

    private static long readLongBE(byte[] data, int offset) {
        return ((data[offset] & 0xffL) << 56)
                | ((data[offset + 1] & 0xffL) << 48)
                | ((data[offset + 2] & 0xffL) << 40)
                | ((data[offset + 3] & 0xffL) << 32)
                | ((data[offset + 4] & 0xffL) << 24)
                | ((data[offset + 5] & 0xffL) << 16)
                | ((data[offset + 6] & 0xffL) << 8)
                | (data[offset + 7] & 0xffL);
    }
}
