package org.powernukkitx.level.generator.object;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockEntityHolder;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntArrayTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.RuntimeBlockDefinition;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.ActorBlockSyncMessageId;
import org.cloudburstmc.protocol.bedrock.data.BlockChangeEntry;
import org.cloudburstmc.protocol.bedrock.packet.UpdateSubChunkBlocksPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class BlockManager {
    private static final String PENDING_SUB_CHUNK_UPDATES = "pendingSubChunkUpdates";

    private final Level level;
    private final Long2ObjectOpenHashMap<Block> caches;
    private final Long2ObjectOpenHashMap<Block> places;
    private IChunk stateChunk0;
    private int stateChunkX0;
    private int stateChunkZ0;
    private IChunk stateChunk1;
    private int stateChunkX1;
    private int stateChunkZ1;

    protected final ObjectOpenHashSet<Runnable> hooks;

    private long hashXYZ(int x, int y, int z, int layer) {
        return (((long) (x + 30_000_000) & 0x3FFFFFFL) << 37)
                | (((long) (z + 30_000_000) & 0x3FFFFFFL) << 11)
                | (((long) (y + 400) & 0x3FFL) << 1)
                | ((long) layer & 0x1L);
    }

    public BlockManager(Level level) {
        this.level = level;
        this.caches = new Long2ObjectOpenHashMap<>();
        this.places = new Long2ObjectOpenHashMap<>();
        this.hooks = new ObjectOpenHashSet<>();
    }

    public void addHook(Runnable runnable) {
        this.hooks.add(runnable);
    }

    public ObjectOpenHashSet<Runnable> getHooks() {
        return this.hooks;
    }

    protected void applyHooks() {
        hooks.parallelStream().forEach(Runnable::run);
        hooks.clear();
    }

    public String getBlockIdIfCachedOrLoaded(int x, int y, int z) {
        return getBlockStateIfCachedOrLoaded(x, y, z).getIdentifier();
    }

    private IChunk getStateChunkIfLoaded(int chunkX, int chunkZ) {
        if (this.stateChunk0 != null && this.stateChunkX0 == chunkX && this.stateChunkZ0 == chunkZ) {
            return this.stateChunk0;
        }
        if (this.stateChunk1 != null && this.stateChunkX1 == chunkX && this.stateChunkZ1 == chunkZ) {
            IChunk chunk = this.stateChunk1;
            this.stateChunk1 = this.stateChunk0;
            this.stateChunkX1 = this.stateChunkX0;
            this.stateChunkZ1 = this.stateChunkZ0;
            this.stateChunk0 = chunk;
            this.stateChunkX0 = chunkX;
            this.stateChunkZ0 = chunkZ;
            return chunk;
        }

        IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
        if (chunk != null) {
            this.stateChunk1 = this.stateChunk0;
            this.stateChunkX1 = this.stateChunkX0;
            this.stateChunkZ1 = this.stateChunkZ0;
            this.stateChunk0 = chunk;
            this.stateChunkX0 = chunkX;
            this.stateChunkZ0 = chunkZ;
        }
        return chunk;
    }

    /**
     * Returns the queued or loaded block state without materializing a Block.
     */
    public BlockState getBlockStateIfCachedOrLoaded(int x, int y, int z) {
        Block cached = this.caches.get(hashXYZ(x, y, z, 0));
        if (cached != null) {
            return cached.getBlockState();
        }
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return BlockAir.STATE;
        }

        IChunk chunk = getStateChunkIfLoaded(x >> 4, z >> 4);
        return chunk == null ? BlockAir.STATE : chunk.getBlockState(x & 0x0f, y, z & 0x0f);
    }

    public String getBlockIdAt(int x, int y, int z) {
        return this.getBlockIdAt(x, y, z, 0);
    }

    public String getBlockIdAt(int x, int y, int z, int layer) {
        Block cached = this.caches.get(hashXYZ(x, y, z, layer));
        if (cached != null) {
            return cached.getId();
        }
        return level.getBlockStateAt(x, y, z, layer).getIdentifier();
    }

    public Block getBlockIfCachedOrLoaded(Vector3 vector3) {
        return getBlockIfCachedOrLoaded(vector3.getFloorX(), vector3.getFloorY(), vector3.getFloorZ());
    }

    public Block getBlockIfCachedOrLoaded(int x, int y, int z, BlockState fallback) {
        long hash = hashXYZ(x, y, z, 0);
        if (caches.containsKey(hash)) {
            return caches.get(hash);
        }
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (level.isChunkLoaded(chunkX, chunkZ)) {
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
        return getCachedBlock(x, y, z, BlockAir.STATE.toBlock(new Position(x, y, z, level)));
    }

    public Block getCachedBlock(int x, int y, int z, Block fallback) {
        return this.caches.getOrDefault(hashXYZ(x, y, z, 0), fallback);
    }

    public void setBlockStateAt(Vector3 blockVector3, BlockState blockState) {
        this.setBlockStateAt(blockVector3.getFloorX(), blockVector3.getFloorY(), blockVector3.getFloorZ(), blockState);
    }

    public boolean setBlockStateAtIfCacheAbsent(BlockVector3 blockVector3, BlockState blockState) {
        long hash = hashXYZ(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ(), 0);
        if (!this.caches.containsKey(hash)) {
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

    public boolean isCached(BlockVector3 blockVector3) {
        return isCached(blockVector3, 0);
    }

    public boolean isCached(BlockVector3 blockVector3, int layer) {
        long hash = hashXYZ(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ(), layer);
        return caches.containsKey(hash);
    }

    public void merge(BlockManager manager) {
        if (manager.places.isEmpty()) {
            this.hooks.addAll(manager.getHooks());
            return;
        }
        if (this.level == manager.level) {
            this.places.putAll(manager.places);
            this.caches.putAll(manager.places);
        } else {
            for (Block block : manager.places.values()) {
                this.setBlockStateAt(
                        block.getFloorX(),
                        block.getFloorY(),
                        block.getFloorZ(),
                        block.layer,
                        block.getBlockState()
                );
            }
        }
        this.hooks.addAll(manager.getHooks());
    }

    /**
     * Merges valid blocks targeting generated chunks without rematerializing same-level blocks.
     */
    public void mergeGeneratedBlocks(BlockManager manager) {
        if (manager.places.isEmpty()) {
            this.hooks.addAll(manager.getHooks());
            return;
        }

        if (this.level == manager.level) {
            for (var entry : manager.places.long2ObjectEntrySet()) {
                Block block = entry.getValue();
                IChunk blockChunk = level.getChunk(block.getChunkX(), block.getChunkZ());
                if (block.isValid() && blockChunk != null && blockChunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING) {
                    long hash = entry.getLongKey();
                    this.places.put(hash, block);
                    this.caches.put(hash, block);
                }
            }
        } else {
            for (Block block : manager.places.values()) {
                IChunk blockChunk = level.getChunk(block.getChunkX(), block.getChunkZ());
                if (block.isValid() && blockChunk != null && blockChunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING) {
                    this.setBlockStateAt(
                            block.getFloorX(),
                            block.getFloorY(),
                            block.getFloorZ(),
                            block.layer,
                            block.getBlockState()
                    );
                }
            }
        }
        this.hooks.addAll(manager.getHooks());
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

    public AxisAlignedBB getBounds() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Block block : this.places.values()) {
            minX = Math.min(minX, block.getFloorX());
            minY = Math.min(minY, block.getFloorY());
            minZ = Math.min(minZ, block.getFloorZ());
            maxX = Math.max(maxX, block.getFloorX());
            maxY = Math.max(maxY, block.getFloorY());
            maxZ = Math.max(maxZ, block.getFloorZ());
        }

        return new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
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
            value.forEach(b -> {
                key.setBlockState(b.getFloorX() & 15, b.getFloorY(), b.getFloorZ() & 15, b.getBlockState(), b.layer);
            });
        });
        applyHooks();
    }

    public void applyBlockUpdate() {
        for (var b : this.places.values()) {
            this.level.setBlock(b, b, true, true);
        }
        applyHooks();
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void generateChunks() {
        for (Block block : this.getBlocks()) {
            if (block.getChunk().getFinalizationState() == ChunkFinalizationState.NEEDS_INSTATICKING) {
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
        this.applySubChunkUpdate(blockList, predicate, true);
    }

    public void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate, boolean queueSave) {
        if (predicate != null) {
            ArrayList<Block> filtered = new ArrayList<>(blockList.size());
            for (Block block : blockList) {
                if (predicate.test(block)) {
                    filtered.add(block);
                }
            }
            blockList = filtered;
        }
        if (blockList.isEmpty()) {
            applyHooks();
            places.clear();
            caches.clear();
            return;
        }

        LongOpenHashSet retainedChunks = new LongOpenHashSet();
        for (Block block : blockList) {
            long chunkHash = Level.chunkHash(block.getChunkX(), block.getChunkZ());
            if (retainedChunks.add(chunkHash)) {
                level.retainGenerationTask(chunkHash);
            }
        }

        try {
            HashMap<IChunk, ArrayList<Block>> chunks = new HashMap<>(Math.max(16, blockList.size() >> 3));
            Set<Long> deferredChunks = ConcurrentHashMap.newKeySet();
            boolean shouldBroadcast = !level.getPlayers().isEmpty();
            HashMap<SubChunkEntry, UpdateSubChunkBlocksPacket> batchs =
                    shouldBroadcast ? new HashMap<>(Math.max(16, blockList.size() >> 2)) : null;
            HashMap<Long, List<Player>> recipientsByChunk = shouldBroadcast ? new HashMap<>() : null;

            for (var b : blockList) {
                ArrayList<Block> chunk =
                        chunks.computeIfAbsent(level.getChunk(b.getChunkX(), b.getChunkZ(), true), c -> new ArrayList<>());
                chunk.add(b);
                if (shouldBroadcast) {
                    long chunkHash = Level.chunkHash(b.getChunkX(), b.getChunkZ());
                    List<Player> recipients = recipientsByChunk.computeIfAbsent(chunkHash, hash -> {
                        List<Player> result = new ArrayList<>();
                        for (Player player : level.getChunkPlayers(b.getChunkX(), b.getChunkZ()).values()) {
                            if (player.isConnected() && player.getPlayerChunkManager().isSentChunk(hash)) {
                                result.add(player);
                            }
                        }
                        return result;
                    });
                    if (recipients.isEmpty()) {
                        continue;
                    }
                    UpdateSubChunkBlocksPacket batch = batchs.computeIfAbsent(
                            new SubChunkEntry(b.getChunkX() << 4, (b.getFloorY() >> 4) << 4, b.getChunkZ() << 4),
                            s -> {
                                final UpdateSubChunkBlocksPacket packet = new UpdateSubChunkBlocksPacket();
                                packet.setSubChunkBlockPosition(Vector3i.from(s.x, s.y, s.z));
                                return packet;
                            });
                    if (b.layer == 1) {
                        batch.getExtraBlocks().add(new BlockChangeEntry(
                                b.asBlockVector3().toNetwork(),
                                new RuntimeBlockDefinition((int) b.getBlockState().unsignedBlockStateHash()),
                                0,
                                -1,
                                ActorBlockSyncMessageId.NONE
                        ));
                    } else {
                        batch.getStandardBlocks().add(new BlockChangeEntry(
                                b.asBlockVector3().toNetwork(),
                                new RuntimeBlockDefinition((int) b.getBlockState().unsignedBlockStateHash()),
                                0,
                                -1,
                                ActorBlockSyncMessageId.NONE
                        ));
                    }
                }
            }

            chunks.entrySet().parallelStream().forEach(entry -> {
                final var key = entry.getKey();
                final var value = entry.getValue();

                if (key.getFinalizationState() == ChunkFinalizationState.NEEDS_INSTATICKING) {
                    deferredChunks.add(Level.chunkHash(key.getX(), key.getZ()));
                    queuePendingSubChunkUpdates(key, value);
                    return;
                }
                key.batchProcess(unsafeChunk -> {
                    for (Block b : value) {
                        unsafeChunk.setBlockState(
                                b.getFloorX() & 15,
                                b.getFloorY(),
                                b.getFloorZ() & 15,
                                b.getBlockState(),
                                b.layer
                        );
                    }
                });
                if (queueSave) {
                    key.setChanged();
                }
                key.reObfuscateChunk();
            });

            applyHooks();
            for (var b : blockList) {
                long chunkHash = Level.chunkHash(b.getChunkX(), b.getChunkZ());
                if (!deferredChunks.contains(chunkHash) && b instanceof BlockEntityHolder<?> holder) {
                    holder.getOrCreateBlockEntity();
                }
            }

            if (shouldBroadcast) {
                for (var entry : batchs.entrySet()) {
                    SubChunkEntry subChunk = entry.getKey();
                    long chunkHash = Level.chunkHash(subChunk.x >> 4, subChunk.z >> 4);
                    if (deferredChunks.contains(chunkHash)) {
                        continue;
                    }
                    List<Player> recipients = recipientsByChunk.get(chunkHash);
                    if (recipients == null) {
                        continue;
                    }
                    for (Player player : recipients) {
                        if (player.isConnected() && player.getPlayerChunkManager().isSentChunk(chunkHash)) {
                            player.sendPacket(entry.getValue());
                        }
                    }
                }
            }

            places.clear();
            caches.clear();
        } finally {
            retainedChunks.forEach(level::releaseGenerationTask);
        }
    }

    private void queuePendingSubChunkUpdates(IChunk chunk, List<Block> blocks) {
        synchronized (chunk) {
            CompoundTag extraData = chunk.getExtraData();
            ListTag<IntArrayTag> pending = extraData.containsList(PENDING_SUB_CHUNK_UPDATES, Tag.TAG_Int_Array)
                    ? extraData.getList(PENDING_SUB_CHUNK_UPDATES, IntArrayTag.class)
                    : new ListTag<>(Tag.TAG_Int_Array);
            for (Block block : blocks) {
                pending.add(new IntArrayTag(new int[] {
                        block.getFloorX(),
                        block.getFloorY(),
                        block.getFloorZ(),
                        block.layer,
                        block.getBlockState().blockStateHash()
                }));
            }
            extraData.putList(PENDING_SUB_CHUNK_UPDATES, pending);
            chunk.setChanged();
        }
    }

    public static void applyPendingSubChunkUpdates(Level level, IChunk chunk) {
        if (chunk.getFinalizationState() == ChunkFinalizationState.NEEDS_INSTATICKING) {
            return;
        }

        ListTag<IntArrayTag> pending;
        synchronized (chunk) {
            CompoundTag extraData = chunk.getExtraData();
            if (!extraData.containsList(PENDING_SUB_CHUNK_UPDATES, Tag.TAG_Int_Array)) {
                return;
            }
            pending = extraData.removeAndGet(PENDING_SUB_CHUNK_UPDATES);
        }
        if (pending == null || pending.size() == 0) {
            return;
        }

        BlockManager pendingBlocks = BlockManager.fromTag(pending, new BlockManager(level));
        pendingBlocks.applySubChunkUpdate(pendingBlocks.getBlocks(), null, true);
    }

    public int getMaxHeight() {
        return level.getMaxHeight();
    }

    public int getMinHeight() {
        return level.getMinHeight();
    }

    public List<int[]> toTag() {
        final List<int[]> list = new ObjectArrayList<>();
        for (var b : this.places.values()) {
            list.add(new int[]{
                    b.getFloorX(),
                    b.getFloorY(),
                    b.getFloorZ(),
                    b.layer,
                    b.getBlockState().blockStateHash()
            });
        }
        return list;
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
