package org.powernukkitx.utils;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.property.type.BlockPropertyType;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.TreeMap;

/**
 * Utils for hash
 *
 * @author Cool_Loong (Allay Project)
 * @since 2023/6/1
 */
@UtilityClass
public class HashUtils {
    //https://gist.github.com/Alemiz112/504d0f79feac7ef57eda174b668dd345
    private static final int FNV1_32_INIT = 0x811c9dc5;
    private static final int FNV1_PRIME_32 = 0x01000193;
    private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV1_PRIME_64 = 1099511628211L;

    private static final long XXH64_PRIME1 = 0x9E3779B185EBCA87L;
    private static final long XXH64_PRIME2 = 0xC2B2AE3D27D4EB4FL;
    private static final long XXH64_PRIME3 = 0x165667B19E3779F9L;
    private static final long XXH64_PRIME4 = 0x85EBCA77C2B2AE63L;
    private static final long XXH64_PRIME5 = 0x27D4EB2F165667C5L;

    public int computeBlockStateHash(String identifier, List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> propertyValues) {
        if (identifier.equals(BlockID.UNKNOWN)) {
            return -2; // This is special case
        }

        //build block state tag
        var states = NbtMap.builder();
        for (var value : propertyValues) {
            switch (value.getPropertyType().getType()) {
                case INT -> states.putInt(value.getPropertyType().getName(), (int) value.getSerializedValue());
                case ENUM -> states.putString(value.getPropertyType().getName(), value.getSerializedValue().toString());
                case BOOLEAN -> states.putByte(value.getPropertyType().getName(), (byte) value.getSerializedValue());
            }
        }

        var tag = NbtMap.builder()
                .putString("name", identifier)
                .putCompound("states", NbtMap.fromMap(new TreeMap<>(states.build())))
                .build();
        return fnv1a_32_nbt(tag);
    }

    public int computeBlockStateHash(String identifier, BlockPropertyType.BlockPropertyValue<?, ?, ?>[] propertyValues) {
        if (identifier.equals(BlockID.UNKNOWN)) {
            return -2; // This is special case
        }

        var states = NbtMap.builder();
        for (var value : propertyValues) {
            switch (value.getPropertyType().getType()) {
                case INT -> states.putInt(value.getPropertyType().getName(), (int) value.getSerializedValue());
                case ENUM -> states.putString(value.getPropertyType().getName(), value.getSerializedValue().toString());
                case BOOLEAN -> states.putByte(value.getPropertyType().getName(), (byte) value.getSerializedValue());
            }
        }
        var tag = NbtMap.builder()
                .putString("name", identifier)
                .putCompound("states", NbtMap.fromMap(new TreeMap<>(states.build())))
                .build();
        return fnv1a_32_nbt(tag);
    }


    public static long fnv164(final byte[] data) {
        long hash = FNV1_64_INIT;
        for (final byte datum : data) {
            hash ^= (datum & 0xff);
            hash *= FNV1_PRIME_64;
        }
        return hash;
    }

    @SneakyThrows
    public int fnv1a_32_nbt(NbtMap tag) {
        if (tag.getString("name").equals("minecraft:unknown")) {
            return -2; // This is special case
        }
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(tag);
            nbtOutputStream.close();
            return fnv1a_32(outputStream.toByteArray());
        }
    }

    @SneakyThrows
    public int fnv1a_32_nbt_palette(NbtMap tag) {
        if (tag.getString("name").equals("minecraft:unknown")) {
            return -2; // This is special case
        }
        final TreeMap<String, Object> sorted = new TreeMap<>(tag.getCompound("states"));
        final NbtMap states = NbtMap.fromMap(sorted);
        final NbtMapBuilder builder = NbtMap.builder();
        builder.putString("name", tag.getString("name"));
        builder.putCompound("states", states);
        builder.remove("version");
        tag = builder.build();
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(tag);
            return fnv1a_32(outputStream.toByteArray());
        }
    }

    /**
     * Computes the LevelChunkMetaData dictionary hash for network-NBT metadata.
     *
     * @param metadata metadata compound
     * @return XXH64 metadata hash
     */
    @SneakyThrows
    public long computeLevelChunkMetaDataHash(NbtMap metadata) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createNetworkWriter(outputStream)) {
            nbtOutputStream.writeTag(metadata);
            nbtOutputStream.close();
            return xxh64(outputStream.toByteArray(), 0);
        }
    }

    //CPU Ryzen PRO 5850U, 16G, Win11
    //Throughput 15736.451 ± 337.778  ops/ms
    public int fnv1a_32(final byte[] data) {
        int hash = FNV1_32_INIT;
        for (final byte datum : data) {
            hash ^= (datum & 0xff);
            hash *= FNV1_PRIME_32;
        }
        return hash;
    }

    /**
     * Computes an XXH64 hash with seed zero.
     *
     * @param data input data
     * @return XXH64 hash
     */
    public long xxh64(final byte[] data) {
        return xxh64(data, 0);
    }

    /**
     * Computes an XXH64 hash using the supplied seed.
     *
     * @param data input data
     * @param seed hash seed
     * @return XXH64 hash
     */
    public long xxh64(final byte[] data, long seed) {
        int length = data.length;
        int index = 0;
        long hash;

        if (length >= 32) {
            long v1 = seed + XXH64_PRIME1 + XXH64_PRIME2;
            long v2 = seed + XXH64_PRIME2;
            long v3 = seed;
            long v4 = seed - XXH64_PRIME1;

            int limit = length - 32;

            do {
                v1 = xxh64Round(v1, readLongLE(data, index));
                index += 8;

                v2 = xxh64Round(v2, readLongLE(data, index));
                index += 8;

                v3 = xxh64Round(v3, readLongLE(data, index));
                index += 8;

                v4 = xxh64Round(v4, readLongLE(data, index));
                index += 8;
            } while (index <= limit);

            hash = Long.rotateLeft(v1, 1)
                    + Long.rotateLeft(v2, 7)
                    + Long.rotateLeft(v3, 12)
                    + Long.rotateLeft(v4, 18);

            hash = xxh64MergeRound(hash, v1);
            hash = xxh64MergeRound(hash, v2);
            hash = xxh64MergeRound(hash, v3);
            hash = xxh64MergeRound(hash, v4);
        } else {
            hash = seed + XXH64_PRIME5;
        }

        hash += length;

        while (index <= length - 8) {
            long value = xxh64Round(0, readLongLE(data, index));

            hash ^= value;
            hash = Long.rotateLeft(hash, 27) * XXH64_PRIME1 + XXH64_PRIME4;

            index += 8;
        }

        if (index <= length - 4) {
            hash ^= (readIntLE(data, index) & 0xffffffffL) * XXH64_PRIME1;
            hash = Long.rotateLeft(hash, 23) * XXH64_PRIME2 + XXH64_PRIME3;

            index += 4;
        }

        while (index < length) {
            hash ^= (data[index] & 0xffL) * XXH64_PRIME5;
            hash = Long.rotateLeft(hash, 11) * XXH64_PRIME1;

            index++;
        }

        hash ^= hash >>> 33;
        hash *= XXH64_PRIME2;
        hash ^= hash >>> 29;
        hash *= XXH64_PRIME3;
        hash ^= hash >>> 32;

        return hash;
    }

    private long xxh64Round(long accumulator, long input) {
        accumulator += input * XXH64_PRIME2;
        accumulator = Long.rotateLeft(accumulator, 31);
        accumulator *= XXH64_PRIME1;
        return accumulator;
    }

    private long xxh64MergeRound(long accumulator, long value) {
        accumulator ^= xxh64Round(0, value);
        accumulator = accumulator * XXH64_PRIME1 + XXH64_PRIME4;
        return accumulator;
    }

    private long readLongLE(byte[] data, int index) {
        return (data[index] & 0xffL)
                | ((data[index + 1] & 0xffL) << 8)
                | ((data[index + 2] & 0xffL) << 16)
                | ((data[index + 3] & 0xffL) << 24)
                | ((data[index + 4] & 0xffL) << 32)
                | ((data[index + 5] & 0xffL) << 40)
                | ((data[index + 6] & 0xffL) << 48)
                | ((data[index + 7] & 0xffL) << 56);
    }

    private int readIntLE(byte[] data, int index) {
        return (data[index] & 0xff)
                | ((data[index + 1] & 0xff) << 8)
                | ((data[index + 2] & 0xff) << 16)
                | ((data[index + 3] & 0xff) << 24);
    }

    /**
     * Shift int x to the left by 32 bits and int z to form a long value
     *
     * @param x the int x
     * @param z the int z
     * @return the long
     */
    public long hashXZ(int x, int z) {
        return ((long) x << 32) | (z & 0xffffffffL);
    }

    /**
     * Gets x from {@link #hashXZ(int, int)}
     *
     * @param hashXZ a long value
     */
    public int getXFromHashXZ(long hashXZ) {
        return (int) (hashXZ >> 32);
    }

    /**
     * Gets z from {@link #hashXZ(int, int)}
     *
     * @param hashXZ a long value
     */
    public int getZFromHashXZ(long hashXZ) {
        return (int) hashXZ;
    }

    public int hashChunkXYZ(int x, int y, int z) {
        //Make sure x and z are in the range of 0-15
        x &= 0xF;  //4 bits
        z &= 0xF;  //4 bits
        //Use the int type to store the result
        int result = 0;
        //Place x in the top 4 digits
        result |= (x << 28);
        //Place y in the middle 24 bits
        result |= (y & 0xFFFFFF) << 4;
        //Place z in the lowest 4 digits
        result |= z;
        return result;
    }

    /**
     * Extract the value of x from the hash chunk xyz.
     * x occupies the highest 4 bits.
     *
     * @param encoded Encoded int containing x, y, and z.
     * @return The value of x.
     */
    public int getXFromHashChunkXYZ(int encoded) {
        return (encoded >>> 28);
    }

    /**
     * Extract the value of y from the hash chunk xyz.
     * y occupies the middle 24 bits.
     *
     * @param encoded Encoded int containing x, y, and z.
     * @return The value of y.
     */
    public int getYFromHashChunkXYZ(int encoded) {
        return (encoded >>> 4) & 0xFFFFFF;
    }

    /**
     * Extract the value of z from the hash chunk xyz.
     * z occupies the lowest 4 bits.
     *
     * @param encoded Encoded int containing x, y, and z.
     * @return The value of z.
     */
    public static int getZFromHashChunkXYZ(int encoded) {
        return encoded & 0xF;
    }
}
