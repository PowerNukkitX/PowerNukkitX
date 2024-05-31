package cn.nukkit.level.generator.object;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.UpdateSubChunkBlocksPacket;
import cn.nukkit.network.protocol.types.BlockChangeEntry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class BlockManager {
    private final Level level;
    private final Long2ObjectOpenHashMap<Block> caches;
    private final Long2ObjectOpenHashMap<Block> places;

    private long hashXYZ(int x, int y, int z, int layer) {
        long v = layer == 1 ? 0xFFFFFFF : 0x7FFFFFF;
        return (((long) x & v) << 37) | ((long) (level.ensureY(y) + 64) << 28) | ((long) z & (long) 0xFFFFFFF);
    }

    public BlockManager(Level level) {
        this.level = level;
        this.caches = new Long2ObjectOpenHashMap<>();
        this.places = new Long2ObjectOpenHashMap<>();
    }

    public String getBlockIdAt(int x, int y, int z) {
        return this.getBlockIdAt(x, y, z, 0);
    }

    public String getBlockIdAt(int x, int y, int z, int layer) {
        Block block = this.caches.computeIfAbsent(hashXYZ(x, y, z, layer), k -> level.getBlock(x, y, z, layer));
        return block.getId();
    }

    public Block getBlockAt(int x, int y, int z) {
        return this.caches.computeIfAbsent(hashXYZ(x, y, z, 0), k -> level.getBlock(x, y, z));
    }

    public void setBlockStateAt(Vector3 blockVector3, BlockState blockState) {
        this.setBlockStateAt(blockVector3.getFloorX(), blockVector3.getFloorY(), blockVector3.getFloorZ(), blockState);
    }

    public void setBlockStateAt(BlockVector3 blockVector3, BlockState blockState) {
        this.setBlockStateAt(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ(), blockState);
    }

    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        long hashXYZ = hashXYZ(x, y, z, 0);
        Block block = Block.get(state, level, x, y, z, 0);
        places.put(hashXYZ, block);
        caches.put(hashXYZ, block);
    }

    public void setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        long hashXYZ = hashXYZ(x, y, z, layer);
        Block block = Block.get(state, level, x, y, z, layer);
        places.put(hashXYZ, block);
        caches.put(hashXYZ, block);
    }

    public void setBlockStateAt(int x, int y, int z, String blockId) {
        long hashXYZ = hashXYZ(x, y, z, 0);
        Block block = Block.get(blockId, level, x, y, z, 0);
        places.put(hashXYZ, block);
        caches.put(hashXYZ, block);
    }

    public Level getLevel() {
        return level;
    }

    public IChunk getChunk(int chunkX, int chunkZ) {
        return this.level.getChunk(chunkX, chunkZ);
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
        return new ArrayList<>(this.places.values());
    }

    public void applyBlockUpdate() {
        for (var b : this.places.values()) {
            this.level.setBlock(b, b, true, true);
        }
    }

    public void applySubChunkUpdate() {
        this.applySubChunkUpdate(new ArrayList<>(this.places.values()), null);
    }

    public void applySubChunkUpdate(List<Block> blockList) {
        this.applySubChunkUpdate(blockList, null);
    }

    public void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate) {
        if (predicate != null) {
            blockList = blockList.stream().filter(predicate).toList();
        }
        HashMap<IChunk, ArrayList<Block>> chunks = new HashMap<>();
        HashMap<SubChunkEntry, UpdateSubChunkBlocksPacket> batchs = new HashMap<>();
        for (var b : blockList) {
            ArrayList<Block> chunk = chunks.computeIfAbsent(level.getChunk(b.getChunkX(), b.getChunkZ(), true), c -> new ArrayList<>());
            chunk.add(b);
            UpdateSubChunkBlocksPacket batch = batchs.computeIfAbsent(new SubChunkEntry(b.getChunkX() << 4, (b.getFloorY() >> 4) << 4, b.getChunkZ() << 4), s -> new UpdateSubChunkBlocksPacket(s.x, s.y, s.z));
            if (b.layer == 1) {
                batch.extraBlocks.add(new BlockChangeEntry(b.asBlockVector3(), b.getBlockState().unsignedBlockStateHash(), UpdateBlockPacket.NETWORK_ID, -1, BlockChangeEntry.MessageType.NONE));
            } else {
                batch.standardBlocks.add(new BlockChangeEntry(b.asBlockVector3(), b.getBlockState().unsignedBlockStateHash(), UpdateBlockPacket.NETWORK_ID, -1, BlockChangeEntry.MessageType.NONE));
            }
        }
        chunks.entrySet().parallelStream().forEach(entry -> {
            final var key = entry.getKey();
            final var value = entry.getValue();
            key.batchProcess(unsafeChunk -> {
                value.forEach(b -> {
                    unsafeChunk.setBlockState(b.getFloorX() & 15, b.getFloorY(), b.getFloorZ() & 15, b.getBlockState(), b.layer);
                });
                unsafeChunk.recalculateHeightMap();
            });
            key.reObfuscateChunk();
        });
        for (var p : batchs.values()) {
            Server.broadcastPacket(level.getPlayers().values(), p);
        }
        places.clear();
        caches.clear();
    }

    public int getMaxHeight() {
        return level.getMaxHeight();
    }

    public int getMinHeight() {
        return level.getMinHeight();
    }

    private record SubChunkEntry(int x, int y, int z) {
    }
}
