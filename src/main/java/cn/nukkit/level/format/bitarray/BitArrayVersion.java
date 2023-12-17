package cn.nukkit.level.format.bitarray;

import cn.nukkit.math.NukkitMath;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
public enum BitArrayVersion {

    V16(16, 2, null),
    V8(8, 4, V16),
    V6(6, 5, V8), // 2 bit padding
    V5(5, 6, V6), // 2 bit padding
    V4(4, 8, V5),
    V3(3, 10, V4), // 2 bit padding
    V2(2, 16, V3),
    V1(1, 32, V2),
    V0(0, 0, V1);

    private static final BitArrayVersion[] VALUES = values();

    public final byte bits;
    public final byte entriesPerWord;
    public final int maxEntryValue;
    public final BitArrayVersion next;

    BitArrayVersion(int bits, int entriesPerWord, BitArrayVersion next) {
        this.bits = (byte) bits;
        this.entriesPerWord = (byte) entriesPerWord;
        this.maxEntryValue = (1 << this.bits) - 1;
        this.next = next;
    }

    public static BitArrayVersion get(int version, boolean read) {
        for (BitArrayVersion ver : values())
            if ((!read && ver.entriesPerWord <= version) || (read && ver.bits == version))
                return ver;

        if (version == 0x7F && read) return null;
        throw new IllegalArgumentException("Invalid palette version: " + version);
    }

    public static BitArrayVersion forBitsCeil(int bits) {
        for (int i = VALUES.length - 1; i >= 0; i--) {
            final BitArrayVersion version = VALUES[i];
            if (version.bits >= bits) return version;
        }

        return null;
    }

    public int getWordsForSize(int size) {
        return NukkitMath.ceilFloat((float) size / this.entriesPerWord);
    }

    public BitArray createArray(int size) {
        return this.createArray(size, new int[NukkitMath.ceilFloat((float) size / this.entriesPerWord)]);
    }

    public BitArray createArray(int size, int[] words) {
        if (this == V3 || this == V5 || this == V6)
            return new PaddedBitArray(this, size, words);
        else if (this == V0)
            return new SingletonBitArray();
        else
            return new Pow2BitArray(this, size, words);
    }

}
