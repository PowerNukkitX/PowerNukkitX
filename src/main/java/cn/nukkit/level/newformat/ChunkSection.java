package cn.nukkit.level.newformat;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.newformat.palette.Palette;
import io.netty.buffer.ByteBuf;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.locks.StampedLock;

import static cn.nukkit.level.newformat.IChunk.index;

/**
 * Allay Project 2023/5/30
 *
 * @author Cool_Loong
 */
@NotThreadSafe
public record ChunkSection(byte sectionY,
                           Palette<BlockState>[] blockLayer,
                           Palette<Integer> biomes,
                           NibbleArray blockLights,
                           NibbleArray skyLights,
                           StampedLock lock) {
    public static final int SIZE = 16 * 16 * 16;
    public static final int LAYER_COUNT = 2;
    public static final int VERSION = 9;

    public ChunkSection(byte sectionY) {
        this(sectionY,
                new Palette[]{new Palette<>(BlockAir.PROPERTIES.getDefaultState()), new Palette<>(BlockAir.PROPERTIES.getDefaultState())},
                new Palette<>(1),
                new NibbleArray(SIZE),
                new NibbleArray(SIZE),
                new StampedLock());
    }

    public ChunkSection(byte sectionY, Palette<BlockState>[] blockLayer) {
        this(sectionY, blockLayer,
                new Palette<>(1),
                new NibbleArray(SIZE),
                new NibbleArray(SIZE),
                new StampedLock());
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        long stamp = lock.tryOptimisticRead();
        try {
            for (; ; stamp = lock.readLock()) {
                if (stamp == 0L) continue;
                BlockState result = blockLayer[layer].get(index(x, y, z));
                if (!lock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) lock.unlockRead(stamp);
        }
    }

    public void setBlockState(int x, int y, int z, BlockState blockState, int layer) {
        long stamp = lock.writeLock();
        try {
            blockLayer[layer].set(index(x, y, z), blockState);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public BlockState getAndSetBlock(int x, int y, int z, BlockState blockstate, int layer) {
        long stamp = lock.writeLock();
        try {
            BlockState result = blockLayer[layer].get(index(x, y, z));
            blockLayer[layer].set(index(x, y, z), blockstate);
            return result;
        } finally {
            lock.unlockWrite(stamp);
        }
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

    public byte getSkyLight(int x, int y, int z) {
        return skyLights.get(index(x, y, z));
    }

    public void setBlockLight(int x, int y, int z, byte light) {
        blockLights.set(index(x, y, z), light);
    }

    public void setSkyLight(int x, int y, int z, byte light) {
        skyLights.set(index(x, y, z), light);
    }

    public boolean isEmpty() {
        return blockLayer[0].isEmpty() && blockLayer[0].get(0) == BlockAir.PROPERTIES.getDefaultState();
    }

    public void writeToNetwork(ByteBuf byteBuf) {
        long stamp = lock.writeLock();
        try {
            byteBuf.writeByte(VERSION);
            //block layer count
            byteBuf.writeByte(LAYER_COUNT);
            byteBuf.writeByte(sectionY & 0xFF);

            blockLayer[0].writeToNetwork(byteBuf, BlockState::blockStateHash);
            blockLayer[1].writeToNetwork(byteBuf, BlockState::blockStateHash);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
