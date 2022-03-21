package cn.nukkit.level.format.leveldb.palette;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class Palette<T> implements Cloneable {
    private BiMap<Integer, T> entries = HashBiMap.create();
    private int paletteEntries = 0;


    /**
     * Add a new block state to this palette.
     * @param entry the entry to be added
     */
    public void addEntry(T entry) {
        if (!this.entries.inverse().containsKey(entry)) {
            this.entries.put(this.paletteEntries++, entry);
        }
    }

    /**
     * Retrieve the total palette size.
     * @return length of the palette
     */
    public int size() {
        return this.entries.size();
    }

    /**
     * Retrieve all of the current palette entries.
     * @return set of all block palette entries in this palette
     */
    public Set<T> getEntries() {
        return Collections.unmodifiableSet(this.entries.values());
    }

    /**
     * Remove a entry from the palette.
     * @param entry the entry to remove
     */
    public void removeEntry(T entry) {
        this.entries.inverse().remove(entry);
    }

    /**
     * Retrieve a entry given the index.
     * @param index the index of the entry
     * @return the entry associated with that index
     */
    public T getEntry(int index) {
        return this.entries.get(index);
    }

    /**
     * Retrieve the index associated with the entry.
     * @param entry the entry
     * @return the index associated with the entry
     */
    public int getPaletteIndex(T entry) {
        return this.entries.inverse().get(entry);
    }

    /**
     * Resize modifies the block palette indexes in order to take as less space as possible.
     * Unused palette entries are shifted.
     */
    public void resize() {
        int resizeStartingIndex = -1;   // The first entry index that we need to relocate
        for (int index = 0; index < this.paletteEntries; index++) {
            if (!this.entries.containsKey(index)) {
                resizeStartingIndex = index + 1;
                break;
            }
        }

        if (resizeStartingIndex > -1) {
            int oldTotalPaletteEntries = this.paletteEntries;
            int freeIndexAt = resizeStartingIndex - 1;  // New entry position - incremented everytime we relocate a entry
            for (int index = resizeStartingIndex; index < oldTotalPaletteEntries; index++) {
                if (this.entries.containsKey(index)) {
                    T entry = this.entries.remove(index);
                    this.entries.put(freeIndexAt++, entry);
                } else {
                    // Another entry was removed
                    this.paletteEntries--;
                }
            }
            this.paletteEntries--;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Palette<T> clone() {
        try {
            Palette<T> clonedPalette = (Palette<T>) super.clone();
            clonedPalette.entries = HashBiMap.create(this.entries);

            return clonedPalette;
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError("Clone threw exception");
        }
    }

    public void writeTo(BinaryStream stream) {
        var entries = this.entries.values();
        stream.putLInt(entries.size());
        // TODO: 2022/3/21 完成方块写入
    }
}
