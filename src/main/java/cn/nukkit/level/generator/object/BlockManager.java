package cn.nukkit.level.generator.object;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.UpdateSubChunkBlocksPacket;
import cn.nukkit.network.protocol.types.BlockChangeEntry;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.logging.log4j.util.InternalApi;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class BlockManager {
    private final Level level;
    private final Long2ObjectOpenHashMap<Block> caches;
    private final Long2ObjectOpenHashMap<Block> places;

    private long hashXYZ(int x, int y, int z, int layer) {
        //generate unique hash for x,y,z,layer: +- 30M for x,z and +-400 for y, layer 0-1
        return
                (((long) (x + 30000000)) & 0x3FFFFFFL) << 38 |
                        (((long) (z + 30000000)) & 0x3FFFFFFL) << 12 |
                        (((long) (y + 400)) & 0xFFF) << 1 |
                        ((long) layer & 0x1);
    }

    public BlockManager(Level level) {
        this.level = level;
        this.caches = new Long2ObjectOpenHashMap<>();
        this.places = new Long2ObjectOpenHashMap<>();
    }


    public String getBlockIdIfCachedOrLoaded(int x, int y, int z) {
        return getBlockIfCachedOrLoaded(x, y, z).getId();
    }

    public String getBlockIdAt(int x, int y, int z) {
        return this.getBlockIdAt(x, y, z, 0);
    }

    public String getBlockIdAt(int x, int y, int z, int layer) {
        Block block = this.caches.computeIfAbsent(hashXYZ(x, y, z, layer), k -> level.getBlock(x, y, z, layer));
        return block.getId();
    }



    public Block getBlockIfCachedOrLoaded(Vector3 vector3) {
        return getBlockIfCachedOrLoaded(vector3.getFloorX(), vector3.getFloorY(), vector3.getFloorZ());
    }

    public Block getBlockIfCachedOrLoaded(int x, int y, int z, BlockState fallback) {
        long hash = hashXYZ(x, y, z, 0);
        if(caches.containsKey(hash)) {
            return caches.get(hash);
        }
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if(level.isChunkLoaded(chunkX, chunkZ)) {
            return getBlockAt(x, y, z);
        }
        return fallback.toBlock(new Position(x, y, z, level));
    }

    public Block getBlockIfCachedOrLoaded(int x, int y, int z) {
        return getBlockIfCachedOrLoaded(x, y, z, BlockAir.STATE);
    }

    public Block getBlockAt(Vector3 vector3) {
        return getBlockAt(vector3.getFloorX(), vector3.getFloorY(), vector3.getFloorZ());
    }

    public Block getBlockAt(int x, int y, int z) {
        return this.caches.computeIfAbsent(hashXYZ(x, y, z, 0), k -> level.getBlock(x, y, z));
    }

    public Block getCachedBlock(Vector3 vector3) {
        return getCachedBlock(vector3.getFloorX(), vector3.getFloorY(), vector3.getFloorZ());
    }

    public Block getCachedBlock(int x, int y, int z) {
        return this.caches.getOrDefault(hashXYZ(x, y, z, 0), BlockAir.STATE.toBlock(new Position(x, y, z, level)));
    }

    public void setBlockStateAt(Vector3 blockVector3, BlockState blockState) {
        this.setBlockStateAt(blockVector3.getFloorX(), blockVector3.getFloorY(), blockVector3.getFloorZ(), blockState);
    }

    public boolean setBlockStateAtIfCacheAbsent(BlockVector3 blockVector3, BlockState blockState) {
        long hash = hashXYZ(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ(), 0);
        if(!this.caches.containsKey(hash)) {
            setBlockStateAt(blockVector3, blockState);
            return true;
        }
        return false;
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

    public void unsetBlockStateAt(Block block) {
        this.unsetBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.layer);
    }

    public void unsetBlockStateAt(int x, int y, int z, int layer) {
        long hashXYZ = hashXYZ(x, y, z, layer);
        places.remove(hashXYZ);
        caches.remove(hashXYZ);
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

    public void merge(BlockManager manager) {
        manager.getBlocks().forEach(b -> this.setBlockStateAt(b, b.getBlockState()));
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

    public void applyWithoutUpdate() {
        HashMap<IChunk, ArrayList<Block>> chunks = new HashMap<>();
        for (var b : places.values()) {
            ArrayList<Block> chunk = chunks.computeIfAbsent(level.getChunk(b.getChunkX(), b.getChunkZ(), true), c -> new ArrayList<>());
            chunk.add(b);
        }
        chunks.entrySet().parallelStream().forEach(entry -> {
            final var key = entry.getKey();
            final var value = entry.getValue();
            key.batchProcess(unsafeChunk -> {
                value.forEach(b -> {
                    unsafeChunk.setBlockState(b.getFloorX() & 15, b.getFloorY(), b.getFloorZ() & 15, b.getBlockState(), b.layer);
                });
            });
        });
    }

    public void applyBlockUpdate() {
        for (var b : this.places.values()) {
            this.level.setBlock(b, b, true, true);
        }
    }

    public void generateChunks() {
        for(Block block : this.getBlocks()) {
            if(!block.getChunk().isGenerated()) {
                block.getLevel().syncGenerateChunk(block.getChunkX(), block.getChunkZ());
            }
        }
    }

    public void applySubChunkUpdate() {
        this.applySubChunkUpdate(new ArrayList<>(this.places.values()), null);
    }

    public void applySubChunkUpdate(List<Block> blockList) {
        this.applySubChunkUpdate(blockList, null);
    }

    public void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate) {
        this.applySubChunkUpdate(blockList, predicate, false);
    }

    public void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate, boolean queueSave) {
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
                batch.extraBlocks.add(new BlockChangeEntry(b.asBlockVector3(), b.getBlockState().unsignedBlockStateHash(), ProtocolInfo.UPDATE_BLOCK_PACKET, -1, BlockChangeEntry.MessageType.NONE));
            } else {
                batch.standardBlocks.add(new BlockChangeEntry(b.asBlockVector3(), b.getBlockState().unsignedBlockStateHash(), ProtocolInfo.UPDATE_BLOCK_PACKET, -1, BlockChangeEntry.MessageType.NONE));
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
            if (queueSave) {
                key.setChanged();
            }
            key.reObfuscateChunk();
        });
        for (var b : blockList) {
            if(b instanceof BlockEntityHolder<?> holder) {
                holder.getOrCreateBlockEntity();
            }
        }
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

    public ListTag<IntArrayTag> toTag() {
        ListTag<IntArrayTag> tag = new ListTag<>();
        for (var b : this.places.values()) {
            tag.add(new IntArrayTag(new int[] {
                    b.getFloorX(),
                    b.getFloorY(),
                    b.getFloorZ(),
                    b.layer,
                    b.getBlockState().blockStateHash()
            }));
        }
        return tag;
    }

    public static BlockManager fromTag(ListTag<IntArrayTag> tag, BlockManager level) {
        for(var data : tag.getAll()) {
            int[] array = data.getData();
            int x = array[0];
            int y = array[1];
            int z = array[2];
            int layer = array[3];
            int blockHash = array[4];
            BlockState state = Registries.BLOCKSTATE.get(blockHash);
            level.setBlockStateAt(x, y, z, layer, state);
        }
        return level;
    }

    private record SubChunkEntry(int x, int y, int z) {
    }
}
