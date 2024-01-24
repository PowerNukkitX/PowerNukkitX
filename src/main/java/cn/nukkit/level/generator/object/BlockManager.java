package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;
import java.util.function.Predicate;

public class BlockManager {
    private final Level level;
    private final Int2ObjectOpenHashMap<Block> blocks;

    private static int hashXYZ(int x, int y, int z, int layer) {
        return (x ^ (z << 12)) ^ (y << 24) + layer;
    }

    public BlockManager(Level level) {
        this.level = level;
        this.blocks = new Int2ObjectOpenHashMap<>();
    }

    public String getBlockIdAt(int x, int y, int z) {
        return this.getBlockIdAt(x, y, z, 0);
    }

    public String getBlockIdAt(int x, int y, int z, int layer) {
        Block block = this.blocks.computeIfAbsent(hashXYZ(x, y, z, 0), k -> level.getBlock(x, y, z));
        return block.getId();
    }

    public void setBlockStateAt(Vector3 blockVector3, BlockState blockState) {
        this.setBlockStateAt(blockVector3.getFloorX(), blockVector3.getFloorY(), blockVector3.getFloorZ(), blockState);
    }

    public void setBlockStateAt(BlockVector3 blockVector3, BlockState blockState) {
        this.setBlockStateAt(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ(), blockState);
    }

    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        blocks.put(hashXYZ(x, y, z, 0), Block.get(state, level, x, y, z, 0));
    }

    public void setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        blocks.put(hashXYZ(x, y, z, layer), Block.get(state, level, x, y, z, layer));
    }

    public void setBlockAt(Vector3 vector3, Block block) {
        block.x = vector3.x;
        block.y = vector3.y;
        block.z = vector3.z;
        blocks.put(hashXYZ(vector3.getFloorX(), vector3.getFloorY(), vector3.getFloorZ(), 0), block);
    }

    public void setBlockAt(BlockVector3 blockVector3, Block block) {
        block.x = blockVector3.x;
        block.y = blockVector3.y;
        block.z = blockVector3.z;
        blocks.put(hashXYZ(blockVector3.x, blockVector3.y, blockVector3.z, 0), block);
    }

    public void setBlockAt(int x, int y, int z, Block block) {
        block.x = x;
        block.y = y;
        block.z = z;
        blocks.put(hashXYZ(x, y, z, 0), block);
    }

    public void setBlockAt(int x, int y, int z, String block) {
        blocks.put(hashXYZ(x, y, z, 0), Block.get(block));
    }

    public Block getBlockAt(int x, int y, int z) {
        return this.blocks.computeIfAbsent(hashXYZ(x, y, z, 0), k -> level.getBlock(x, y, z));
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

    public List<Block> getBlocks() {
        return new ArrayList<>(this.blocks.values());
    }

    public void apply() {
        this.apply(new ArrayList<>(this.blocks.values()), null);
    }

    public void apply(List<Block> blockList) {
        this.apply(blockList, null);
    }

    public void apply(List<Block> blockList, Predicate<Block> predicate) {
        HashMap<IChunk, ArrayList<Block>> chunks = new HashMap<>();
        for (var b : blockList) {
            ArrayList<Block> batch = chunks.computeIfAbsent(level.getChunk(b.getChunkX(), b.getChunkZ(), true), c -> new ArrayList<>());
            batch.add(b);
        }
        chunks.entrySet().parallelStream().forEach(e -> {
            e.getKey().batchProcess(unsafeChunk -> {
                if (predicate == null) {
                    e.getValue().forEach(b -> unsafeChunk.setBlockState(b.getChunkX(), b.getFloorY(), b.getChunkZ(), b.getBlockState(), b.layer));
                } else {
                    e.getValue().stream().filter(predicate).forEach(b -> unsafeChunk.setBlockState(b.getChunkX(), b.getFloorY(), b.getChunkZ(), b.getBlockState(), b.layer));
                }
            });
        });
        blocks.clear();
    }
}
