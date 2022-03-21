package cn.nukkit.level.format.leveldb.datas;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.leveldb.palette.Palette;
import cn.nukkit.utils.BinaryStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class LDBLayer {
    private final Palette<BlockState> palette;
    private final int[] blocks = new int[4096];


    public LDBLayer(Palette<BlockState> palette) {
        this.palette = palette;
    }

    /**
     * Retrieve the {@link Palette} used for this layer.
     * @return the {@link Palette} used for this layer.
     */
    public Palette<BlockState> getPalette() {
        return this.palette;
    }

    /**
     * Retrieve the {@link BlockState} of a block at the given coordinates.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return the {@link BlockState} of the block at the coordinates
     */
    public BlockState getBlockEntryAt(int x, int y, int z) {
        if (this.palette.size() == 0) {
            // if the palette is empty, then add an air entry in order to make this.blocks accurately return air for all 0s.
            this.palette.addEntry(BlockState.AIR);
        }

        return this.palette.getEntry(this.blocks[getBlockIndex(x, y, z)]);
    }

    /**
     * Set the coordinates of the blocklayer to a new {@link BlockState}.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param entry new entry to set the block to
     */
    public void setBlockEntryAt(int x, int y, int z, BlockState entry) {
        if (this.palette.size() == 0) {
            // If the palette is empty, then add an air entry to make every 0 in this.blocks return air.
            // Otherwise, when this method calls this.palette.add(entry), every 0 in this.blocks will be assigned that block.
            this.palette.addEntry(BlockState.AIR);
        }

        this.palette.addEntry(entry);
        this.blocks[getBlockIndex(x, y, z)] = this.palette.getPaletteIndex(entry);
    }

    /**
     * Resize modifies the block layer values and removes unused block palette values.
     */
    public void resize() {
        // Get all the palette indexes being used
        Map<Integer, BlockState> usedEntries = new HashMap<>();
        for (int paletteIndex : this.blocks) {
            usedEntries.put(paletteIndex, this.palette.getEntry(paletteIndex));
        }

        // Remove unused palette entries
        Iterator<BlockState> entryIterator = new HashSet<>(this.palette.getEntries()).iterator();
        while (entryIterator.hasNext()) {
            BlockState entry = entryIterator.next();
            int paletteIndex = this.palette.getPaletteIndex(entry);

            // Air occupies the first element of the block palette and CANNOT be removed or else empty elements of
            // this.blocks will not resolve to air. Any other palette entry can be removed.
            if (!usedEntries.containsKey(paletteIndex) && entry.getBlockId() != (BlockID.AIR)) {
                entryIterator.remove();
                this.palette.removeEntry(entry);
            }
        }

        // Shift entries in the palette as far down as possible
        this.palette.resize();

        // Update our blocks with the new palette indexes
        for (int i = 0; i < this.blocks.length; i++) {
            this.blocks[i] = this.palette.getPaletteIndex(usedEntries.get(this.blocks[i]));
        }
    }

    /**
     * 将方块层数据写入流
     * @param stream 数据流
     */
    public void writeTo(BinaryStream stream) {
        resize();

        int bitsPerBlock = Math.max((int) Math.ceil(Math.log(palette.getEntries().size()) / Math.log(2)), 1);
        int blocksPerWord = 32 / bitsPerBlock;
        int wordsPerChunk = (int) Math.ceil(4096d / blocksPerWord);

        stream.putByte((byte) ((bitsPerBlock << 1) | 1));

        int pos = 0;
        for (int chunk = 0; chunk < wordsPerChunk; chunk++) {
            int word = 0;
            for (int block = 0; block < blocksPerWord; block++) {
                if (pos >= 4096) {
                    break;
                }

                word |= palette.getPaletteIndex(getBlockEntryAt(pos >> 8, pos & 15, (pos >> 4) & 15)) << (bitsPerBlock * block);
                pos++;
            }
            stream.putLInt(word);
        }
        getPalette().writeTo(stream);
    }

    private static int getBlockIndex(int x, int y, int z) {
        return (x << 8) | (z << 4) | y;
    }
}
