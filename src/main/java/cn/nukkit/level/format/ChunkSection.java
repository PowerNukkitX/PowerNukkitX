package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.bitarray.BitArrayVersion;
import cn.nukkit.level.format.palette.BlockPalette;
import cn.nukkit.level.format.palette.Palette;
import cn.nukkit.level.util.NibbleArray;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.registry.Registries;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;

import static cn.nukkit.level.format.IChunk.index;

/**
 * Allay Project 2023/5/30
 *
 * @author Cool_Loong
 */
@NotThreadSafe
public record ChunkSection(byte y,
                           BlockPalette[] blockLayer,
                           Palette<Integer> biomes,
                           NibbleArray blockLights,
                           NibbleArray skyLights,
                           AtomicLong blockChanges) {
    public static final int SIZE = 16 * 16 * 16;
    public static final int LAYER_COUNT = 2;
    public static final int VERSION = 9;

    public ChunkSection(byte sectionY) {
        this(sectionY,
                new BlockPalette[]{new BlockPalette(BlockAir.PROPERTIES.getDefaultState(), new ReferenceArrayList<>(16), BitArrayVersion.V2),
                        new BlockPalette(BlockAir.PROPERTIES.getDefaultState(), new ReferenceArrayList<>(16), BitArrayVersion.V2)},
                new Palette<>(BiomeID.PLAINS),
                new NibbleArray(SIZE),
                new NibbleArray(SIZE),
                new AtomicLong(0));
    }

    public ChunkSection(byte sectionY, BlockPalette[] blockLayer) {
        this(sectionY, blockLayer,
                new Palette<>(BiomeID.PLAINS),
                new NibbleArray(SIZE),
                new NibbleArray(SIZE),
                new AtomicLong(0));
    }

    public BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        return blockLayer[layer].get(index(x, y, z));
    }

    public void setBlockState(int x, int y, int z, BlockState blockState, int layer) {
        blockChanges.addAndGet(1);
        blockLayer[layer].set(index(x, y, z), blockState);
    }

    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        blockChanges.addAndGet(1);
        BlockState result = blockLayer[layer].get(index(x, y, z));
        blockLayer[layer].set(index(x, y, z), blockstate);
        return result;
    }

    public void setBiomeId(int x, int y, int z, int biomeId) {
        biomes.set(index(x, y, z), biomeId);
    }

    public int getBiomeId(int x, int y, int z) {
        return biomes.get(index(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        return blockLights.get(index(x, y, z));
    }

    public byte getBlockSkyLight(int x, int y, int z) {
        return skyLights.get(index(x, y, z));
    }

    public void setBlockLight(int x, int y, int z, byte light) {
        blockLights.set(index(x, y, z), light);
    }

    public void setBlockSkyLight(int x, int y, int z, byte light) {
        skyLights.set(index(x, y, z), light);
    }

    public List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        final List<Block> results = new ArrayList<>();
        final BlockVector3 current = new BlockVector3();
        int offsetY = y << 4;
        int minX = Math.max(0, min.x - offsetX);
        int minY = Math.max(0, min.y - offsetY);
        int minZ = Math.max(0, min.z - offsetZ);
        for (int x = Math.min(max.x - offsetX, 15); x >= minX; x--) {
            current.x = offsetX + x;
            for (int z = Math.min(max.z - offsetZ, 15); z >= minZ; z--) {
                current.z = offsetZ + z;
                for (int y = Math.min(max.y - offsetY, 15); y >= minY; y--) {
                    current.y = offsetY + y;
                    BlockState state = blockLayer[0].get(index(x, y, z));
                    if (condition.test(current, state)) {
                        results.add(Registries.BLOCK.get(state, current.x, current.y, current.z, provider.getLevel()));
                    }
                }
            }
        }
        return results;
    }

    public boolean isEmpty() {
        return blockLayer[0].isEmpty() && blockLayer[0].get(0) == BlockAir.PROPERTIES.getDefaultState();
    }

    public void setNeedReObfuscate() {
        blockLayer[0].setNeedReObfuscate();
        blockLayer[1].setNeedReObfuscate();
    }

    public void writeToBuf(ByteBuf byteBuf) {
        byteBuf.writeByte(VERSION);
        //block layer count
        byteBuf.writeByte(LAYER_COUNT);
        byteBuf.writeByte(y);

        blockLayer[0].writeToNetwork(byteBuf, BlockState::blockStateHash);
        blockLayer[1].writeToNetwork(byteBuf, BlockState::blockStateHash);
    }

    public void writeObfuscatedToBuf(Level level, ByteBuf byteBuf) {
        byteBuf.writeByte(VERSION);
        //block layer count
        byteBuf.writeByte(LAYER_COUNT);
        byteBuf.writeByte(y);

        blockLayer[0].writeObfuscatedToNetwork(level, blockChanges, byteBuf, BlockState::blockStateHash);
        blockLayer[1].writeObfuscatedToNetwork(level, blockChanges, byteBuf, BlockState::blockStateHash);
    }
}
