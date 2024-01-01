package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.IChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ListChunkManager implements ChunkManager {
    private final ChunkManager parent;
    private final List<Block> blocks;


    public ListChunkManager(ChunkManager parent) {
        this.parent = parent;
        this.blocks = new ArrayList<>();
    }

    private Optional<Block> findBlockAt(int x, int y, int z, int layer) {
        return this.blocks.stream().filter(block ->
                block.getFloorX() == x
                        && block.getFloorY() == y
                        && block.getFloorZ() == z
                        && block.layer == layer
        ).findAny();
    }



    @Override
    public void setBlockStateAt(int x, int y, int z, int layer, BlockState state) {

    }

    @Override
    public BlockState getBlockStateAt(int x, int y, int z, int layer) {
        return findBlockAt(x, y, z, layer).map(Block::getBlockState).orElseGet(() -> parent.getBlockStateAt(x, y, z, layer));
    }


    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        return this.parent.getChunk(chunkX, chunkZ);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.parent.setChunk(chunkX, chunkZ);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, IChunk chunk) {

    }

    @Override
    public void setChunk(int chunkX, int chunkZ, IChunk chunk) {
        this.parent.setChunk(chunkX, chunkZ, chunk);
    }

    @Override
    public long getSeed() {
        return this.parent.getSeed();
    }

    @Override
    public void setSeed(long seed) {

    }


    @Override
    public boolean isOverWorld() {
        return parent.isOverWorld();
    }


    @Override
    public boolean isNether() {
        return parent.isNether();
    }


    @Override
    public boolean isTheEnd() {
        return parent.isTheEnd();
    }

    @Override
    public void cleanChunks(long seed) {

    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

}
