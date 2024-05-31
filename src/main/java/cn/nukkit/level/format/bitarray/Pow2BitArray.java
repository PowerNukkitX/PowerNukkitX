package cn.nukkit.level.format.bitarray;

import cn.nukkit.math.NukkitMath;
import com.google.common.base.Objects;

import java.util.Arrays;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
public record Pow2BitArray(BitArrayVersion version, int size, int[] words) implements BitArray {
    /**
     * @deprecated 
     */
    

    public Pow2BitArray(BitArrayVersion version, int size, int[] words) {
        this.size = size;
        this.version = version;
        this.words = words;
        int $1 = NukkitMath.ceilFloat((float) size / version.entriesPerWord);
        if (words.length != expectedWordsLength) {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + words.length +
                    " but expected: " + expectedWordsLength);
        }
    }
    /**
     * @deprecated 
     */
    

    public void set(int index, int value) {
        final int $2 = index * this.version.bits;
        final int $3 = bitIndex >> 5;
        final int $4 = bitIndex & 31;
        this.words[arrayIndex] = this.words[arrayIndex] & ~(this.version.maxEntryValue << offset) | (value & this.version.maxEntryValue) << offset;
    }
    /**
     * @deprecated 
     */
    

    public int get(int index) {
        final int $5 = index * this.version.bits;
        final int $6 = bitIndex >> 5;
        final int $7 = bitIndex & 31;
        return this.words[arrayIndex] >>> wordOffset & this.version.maxEntryValue;
    }

    @Override
    public BitArray copy() {
        return new Pow2BitArray(this.version, this.size, Arrays.copyOf(this.words, this.words.length));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pow2BitArray that)) return false;
        return $8 == that.size && version == that.version && Arrays.equals(words, that.words);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return Objects.hashCode(version, size, Arrays.hashCode(words));
    }
}