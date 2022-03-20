package cn.nukkit.level.format.leveldb.palette;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.Set;

public class IntPalette extends Palette<Integer> {

    private final Int2IntOpenHashMap entries;
    private final Int2IntOpenHashMap inverse;
    private int paletteEntries = 0;

    public IntPalette() {
        this.entries = new Int2IntOpenHashMap();
        this.inverse = new Int2IntOpenHashMap();
    }

    public IntPalette(Int2IntOpenHashMap entries, Int2IntOpenHashMap inverse) {
        this.entries = entries;
        this.inverse = inverse;
    }


    /**
     * Add a new block state to this palette.
     * @param entry the entry to be added
     */
    public void addEntry(int entry) {
        if (!this.inverse.containsValue(entry)) {
            this.entries.put(this.paletteEntries, entry);
            this.inverse.put(entry, this.paletteEntries);
            this.paletteEntries++;
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
    public Set<Integer> getEntries() {
        return IntSets.unmodifiable(new IntArraySet(this.inverse.keySet()));
    }

    /**
     * Remove a entry from the palette.
     * @param entry the entry to remove
     */
    public void removeEntry(int entry) {
        int result = this.inverse.remove(entry);
        this.entries.remove(result);
    }

    /**
     * Retrieve a entry given the index.
     * @param index the index of the entry
     * @return the entry associated with that index
     */
    public Integer getEntry(int index) {
        return this.entries.get(index);
    }

    /**
     * Retrieve a entry given the index.
     * @param index the index of the entry
     * @return the entry associated with that index
     */
    public int fastGetEntry(int index) {
        return this.entries.get(index);
    }

    /**
     * Retrieve the index associated with the entry.
     * @param entry the entry
     * @return the index associated with the entry
     */
    public int getPaletteIndex(int entry) {
        return this.inverse.get(entry);
    }

    /**
     * Resize modifies the block palette indexes in order to take as less space as possible.
     * Unused palette entries are shifted.
     */
    public void resize() {
        entries.trim();
        inverse.trim();

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
                    int entry = this.entries.remove(index);
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
    public IntPalette clone() {
        super.clone();
        var clonedPalette = new IntPalette(this.entries, this.inverse);
        clonedPalette.paletteEntries = this.paletteEntries;
        return clonedPalette;
    }

}
