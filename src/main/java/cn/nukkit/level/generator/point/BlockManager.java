package cn.nukkit.level.generator.point;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;

import java.util.*;

public class BlockManager {
    private final Level level;
    private final Map<BlockVector3, Block> blocks;

    public BlockManager(Level level) {
        this.level = level;
        this.blocks = new HashMap<>();
    }

    public void setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        blocks.put(new BlockVector3(x, y, z), Block.get(state, level, x, y, z, layer));
    }

    public Block getBlockStateAt(int x, int y, int z) {
        return blocks.get(new BlockVector3(x, y, z));
    }

    public IChunk getChunk(int chunkX, int chunkZ) {
        return this.level.getChunk(chunkX, chunkZ);
    }

    public void setChunk(int chunkX, int chunkZ) {
        this.level.setChunk(chunkX, chunkZ);
    }

    public void setChunk(int chunkX, int chunkZ, IChunk chunk) {
        this.level.setChunk(chunkX, chunkZ, chunk);
    }

    public long getSeed() {
        return this.level.getSeed();
    }

    public boolean isOverWorld() {
        return level.isOverWorld();
    }

    public boolean isNether() {
        return level.isNether();
    }

    public boolean isTheEnd() {
        return level.isTheEnd();
    }

    public Collection<Block> getBlocks() {
        return this.blocks.values();
    }
}
