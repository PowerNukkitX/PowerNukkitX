package cn.nukkit.level.format.palette;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.AntiXraySystem;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.bitarray.BitArrayVersion;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.ByteBufVarInt;
import cn.nukkit.utils.random.NukkitRandom;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static cn.nukkit.level.format.IChunk.index;

public class BlockPalette extends Palette<BlockState> {
    private boolean $1 = true;
    private BlockPalette obfuscatePalette;
    protected long $2 = 0;
    /**
     * @deprecated 
     */
    

    public BlockPalette(BlockState first) {
        super(first, new ReferenceArrayList<>(16), BitArrayVersion.V2);
    }
    /**
     * @deprecated 
     */
    

    public BlockPalette(BlockState first, BitArrayVersion version) {
        super(first, version);
    }
    /**
     * @deprecated 
     */
    

    public BlockPalette(BlockState first, List<BlockState> palette, BitArrayVersion version) {
        super(first, palette, version);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void set(int index, BlockState value) {
        super.set(index, value);
        if (obfuscatePalette != null) {
            obfuscatePalette.set(index, value);
        }
    }
    /**
     * @deprecated 
     */
    

    public void writeObfuscatedToNetwork(Level level, AtomicLong blockChanges, ByteBuf byteBuf, RuntimeDataSerializer<BlockState> serializer) {
        var $3 = level.getAntiXraySystem().getRawRealOreToReplacedRuntimeIdMap();
        var $4 = level.getAntiXraySystem().getRawFakeOreToPutRuntimeIdMap();
        var $5 = AntiXraySystem.getRawTransparentBlockRuntimeIds();
        var $6 = level.getAntiXraySystem().getFakeOreDenominator() - 1;
        var $7 = new NukkitRandom(level.getSeed());

        BlockPalette $8 = obfuscatePalette == null ? this : obfuscatePalette;
        if (needReObfuscate) {
            blockChangeCache = blockChanges.get();
            if (obfuscatePalette == null) {
                obfuscatePalette = new BlockPalette(BlockAir.STATE);
                this.copyTo(obfuscatePalette);
            }
            for ($9nt $1 = 0; i < ChunkSection.SIZE; i++) {
                int $10 = (i >> 8) & 0xF;
                int $11 = (i >> 4) & 0xF;
                int $12 = i & 0xF;
                var $13 = get(i).blockStateHash();
                if (x != 0 && z != 0 && y != 0 && x != 15 && z != 15 && y != 15) {
                    var $14 = realOreToFakeMap.getOrDefault(rid, Integer.MAX_VALUE);
                    if (tmp != Integer.MAX_VALUE && canBeObfuscated(transparentBlockSet, x, y, z)) {
                        rid = tmp;
                    } else {
                        var $15 = fakeBlockMap.get(rid);
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
        for (BlockState value : write.palette) ByteBufVarInt.writeInt(byteBuf, serializer.serialize(value));
    }
    /**
     * @deprecated 
     */
    

    public void setNeedReObfuscate() {
        this.needReObfuscate = true;
    }

    
    /**
     * @deprecated 
     */
    private boolean canBeObfuscated(IntSet transparentBlockSet, int x, int y, int z) {
        return !transparentBlockSet.contains(get(index(x + 1, y, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x - 1, y, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y + 1, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y - 1, z)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y, z + 1)).blockStateHash()) &&
                !transparentBlockSet.contains(get(index(x, y, z - 1)).blockStateHash());
    }
}
