package org.powernukkitx.level.format.palette;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockLightProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.AntiXraySystem;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.NukkitRandom;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.powernukkitx.level.format.IChunk.index;

public class BlockPalette extends Palette<BlockState> {
    private static final int LIGHTING_PROPERTIES_UNINITIALIZED = Integer.MIN_VALUE;
    private boolean needReObfuscate = true;
    private BlockPalette obfuscatePalette;
    protected long blockChangeCache = 0;
    private volatile int[] lightingPropertiesCache = new int[0];
    // -1 means unknown/dirty; recomputed lazily on next isEmpty() call
    private int nonAirCount = -1;
    public BlockPalette(BlockState first) {
        super(first, new ReferenceArrayList<>(16), BitArrayVersion.V2);
        this.nonAirCount = get(0) == BlockAir.STATE ? 0 : ChunkSection.SIZE;
    }

    public BlockPalette(BlockState first, BitArrayVersion version) {
        super(first, version);
        this.nonAirCount = get(0) == BlockAir.STATE ? 0 : ChunkSection.SIZE;
    }

    public BlockPalette(BlockState first, List<BlockState> palette, BitArrayVersion version) {
        super(first, palette, version);
        this.nonAirCount = get(0) == BlockAir.STATE ? 0 : ChunkSection.SIZE;
    }

    @Override
    public void set(int index, BlockState value) {
        if (nonAirCount >= 0) {
            updateNonAirCount(get(index), value);
        }
        setInternal(index, value);
    }

    /**
     * Sets a value when the previous state has already been read by the caller.
     */
    public void set(int index, BlockState value, BlockState previousValue) {
        if (nonAirCount >= 0) {
            updateNonAirCount(previousValue, value);
        }
        setInternal(index, value);
    }

    private void updateNonAirCount(BlockState previousValue, BlockState value) {
        boolean wasAir = previousValue == BlockAir.STATE;
        boolean isAir = value == BlockAir.STATE;
        if (wasAir != isAir) nonAirCount += isAir ? -1 : 1;
    }

    private void setInternal(int index, BlockState value) {
        super.set(index, value);
        if (obfuscatePalette != null) {
            obfuscatePalette.set(index, value);
        }
    }

    @Override
    protected void clearPalette() {
        super.clearPalette();
        this.nonAirCount = -1;
        this.lightingPropertiesCache = new int[0];
    }

    @Override
    public void copyTo(Palette<BlockState> palette) {
        super.copyTo(palette);
        if (palette instanceof BlockPalette blockPalette) {
            blockPalette.nonAirCount = this.nonAirCount;
            blockPalette.lightingPropertiesCache = new int[0];
        }
    }

    /**
     * Returns cached packed lighting properties for a palette cell.
     *
     * @param index palette cell index
     * @return packed lighting properties
     */
    public int getLightingProperties(int index) {
        int paletteIndex = this.bitArray.get(index);
        if (paletteIndex >= this.palette.size()) {
            paletteIndex = 0;
        }

        int[] cache = ensureLightingPropertiesCache();
        int packed = cache[paletteIndex];
        if (packed != LIGHTING_PROPERTIES_UNINITIALIZED) {
            return packed;
        }

        packed = BlockLightProperties.packed(this.palette.get(paletteIndex));
        cache[paletteIndex] = packed;
        return packed;
    }

    private int[] ensureLightingPropertiesCache() {
        int requiredSize = this.palette.size();
        int[] cache = this.lightingPropertiesCache;
        if (cache.length == requiredSize) {
            return cache;
        }

        synchronized (this) {
            cache = this.lightingPropertiesCache;
            if (cache.length == requiredSize) {
                return cache;
            }

            int[] resized = new int[requiredSize];
            Arrays.fill(resized, LIGHTING_PROPERTIES_UNINITIALIZED);
            System.arraycopy(cache, 0, resized, 0, Math.min(cache.length, resized.length));
            this.lightingPropertiesCache = resized;
            return resized;
        }
    }

    @Override
    public boolean isEmpty() {
        if (nonAirCount < 0) {
            int count = 0;
            for (int i = 0; i < ChunkSection.SIZE; i++) {
                if (get(i) != BlockAir.STATE) count++;
            }
            nonAirCount = count;
        }
        return nonAirCount == 0;
    }

    public void writeObfuscatedToNetwork(Level level, AtomicLong blockChanges, ByteBuf byteBuf, RuntimeDataSerializer<BlockState> serializer) {
        var realOreToFakeMap = level.getAntiXraySystem().getRawRealOreToReplacedRuntimeIdMap();
        var fakeBlockMap = level.getAntiXraySystem().getRawFakeOreToPutRuntimeIdMap();
        var transparentBlockSet = AntiXraySystem.getRawTransparentBlockRuntimeIds();
        var XAndDenominator = level.getAntiXraySystem().getFakeOreDenominator() - 1;
        var nukkitRandom = new NukkitRandom(level.getSeed());
        BlockPalette write = obfuscatePalette == null ? this : obfuscatePalette;
        if (needReObfuscate) {
            blockChangeCache = blockChanges.get();
            if (obfuscatePalette == null) {
                obfuscatePalette = new BlockPalette(BlockAir.STATE);
                this.copyTo(obfuscatePalette);
            }
            for (int i = 0; i < ChunkSection.SIZE; i++) {
                int x = (i >> 8) & 0xF;
                int z = (i >> 4) & 0xF;
                int y = i & 0xF;
                var rid = get(i).blockStateHash();
                if (x != 0 && z != 0 && y != 0 && x != 15 && z != 15 && y != 15) {
                    var tmp = realOreToFakeMap.getOrDefault(rid, Integer.MAX_VALUE);
                    if (tmp != Integer.MAX_VALUE && canBeObfuscated(transparentBlockSet, x, y, z)) {
                        rid = tmp;
                    } else {
                        var tmp2 = fakeBlockMap.get(rid);
                        if (tmp2 != null && (nukkitRandom.nextInt() & XAndDenominator) == 0 && canBeObfuscated(transparentBlockSet, x, y, z)) {
                            rid = tmp2.getInt(nukkitRandom.nextInt(0, tmp2.size() - 1));
                        }
                    }
                }
                obfuscatePalette.set(i, Registries.BLOCKSTATE.get(rid));
            }
            this.needReObfuscate = false;
            write = obfuscatePalette;
        }

        byteBuf.writeByte(getPaletteHeader(write.bitArray.version(), true));
        for (int word : write.bitArray.words()) byteBuf.writeIntLE(word);
        this.bitArray.writeSizeToNetwork(byteBuf, write.palette.size());
        for (BlockState value : write.palette)
            VarInts.writeInt(byteBuf, serializer.serialize(value));
    }

    public void setNeedReObfuscate() {
        this.needReObfuscate = true;
    }

    private boolean canBeObfuscated(IntSet transparentBlockSet, int x, int y, int z) {
        return !transparentBlockSet.contains(get(index(x + 1, y, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x - 1, y, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y + 1, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y - 1, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y, z + 1)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y, z - 1)).blockStateHash());
    }
}
