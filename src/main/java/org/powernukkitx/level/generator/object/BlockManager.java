package org.powernukkitx.level.generator.object;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockEntityHolder;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.structure.StructureNeighborUpdates;
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
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockManager {
    private static final String PENDING_SUB_CHUNK_UPDATES = "pendingSubChunkUpdates";
    private static final String PENDING_AFTER_APPLY_TICKS = "pendingBlockTicksAfterApply";
    private static final String PENDING_AFTER_APPLY_BLOCK_ENTITIES = "pendingBlockEntitiesAfterApply";

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
    private final ObjectArrayList<int[]> ticksAfterApply;
    private final ObjectArrayList<CompoundTag> blockEntitiesAfterApply;
    private final ObjectArrayList<int[]> neighborsAfterApply;

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
        this.ticksAfterApply = new ObjectArrayList<>();
        this.blockEntitiesAfterApply = new ObjectArrayList<>();
        this.neighborsAfterApply = new ObjectArrayList<>();
    }

    /**
     * Queues a contextual block-state update after its destination block is committed.
     */
    public void queueNeighborAfterApply(int x, int y, int z, int stateHash) {
        neighborsAfterApply.add(new int[]{x, y, z, stateHash});
    }

    /**
     * Queues block-entity data for restoration after the destination block is committed.
     *
     * @param nbt block-entity NBT containing world coordinates
     */
    public void queueBlockEntityAfterApply(CompoundTag nbt) {
        blockEntitiesAfterApply.add(nbt.copy());
    }

    /**
     * Schedules a block tick only after the target block writes have been applied.
     *
     * @param x world X
     * @param y world Y
     * @param z world Z
     * @param delay relative tick delay
     */
    public void queueTickAfterApply(int x, int y, int z, int delay) {
        ticksAfterApply.add(new int[]{x, y, z, Math.max(0, delay)});
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

    /**
     * Returns a queued block when present, otherwise reads the current loaded chunk state without using the block cache.
     */
    public Block getBlockIfQueuedOrLoaded(int x, int y, int z) {
        return getBlockIfQueuedOrLoaded(x, y, z, 0);
    }

    /**
     * Returns a queued block for the requested layer, otherwise reads the current loaded chunk state without using the
     * block cache.
     */
    public Block getBlockIfQueuedOrLoaded(int x, int y, int z, int layer) {
        long hash = hashXYZ(x, y, z, layer);
        Block queued = this.places.get(hash);
        if (queued != null) {
            return queued;
        }

        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return Block.get(BlockAir.STATE, level, x, y, z, layer);
        }

        IChunk chunk = getStateChunkIfLoaded(x >> 4, z >> 4);
        if (chunk == null) {
            return Block.get(BlockAir.STATE, level, x, y, z, layer);
        }

        return Block.get(
                chunk.getBlockState(x & 0x0f, y, z & 0x0f, layer),
                level,
                x,
                y,
                z,
                layer
        );
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
        this.setBlockStateAtUnchecked(x, y, z, state);
    }

    /**
     * Queues a block state without applying GeneratorRoot replacement filtering.
     */
    public final void setBlockStateAtUnchecked(int x, int y, int z, BlockState state) {
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

    /**
     * Queues a block state only when its destination differs or cannot be read safely.
     *
     * @return whether a write was queued
     */
    public boolean setBlockStateIfChanged(int x, int y, int z, int layer, BlockState state) {
        long hash = hashXYZ(x, y, z, layer);
        Block queued = places.get(hash);
        if (queued != null) {
            if (state.equals(queued.getBlockState())) return false;
        } else {
            IChunk chunk = getStateChunkIfLoaded(x >> 4, z >> 4);
            if (chunk != null && chunk.isInitiated()
                    && chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING
                    && state.equals(chunk.getBlockState(x & 15, y, z & 15, layer))) {
                return false;
            }
        }
        setBlockStateAt(x, y, z, layer, state);
        return true;
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

    /**
     * Visits queued generated blocks without materializing a snapshot list.
     *
     * @param consumer block visitor
     */
    public void forEachBlock(Consumer<Block> consumer) {
        this.places.values().forEach(consumer);
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
        this.applySubChunkUpdate(false);
    }

    /**
     * Applies queued blocks with BDS structure synchronization when requested.
     * @param structurePlacement whether the changes belong to structure placement
     */
    public void applySubChunkUpdate(boolean structurePlacement) {
        this.applySubChunkUpdate(new ArrayList<>(this.places.values()), null, true, structurePlacement);
    }

    public void applySubChunkUpdate(List<Block> blockList) {
        this.applySubChunkUpdate(blockList, null);
    }

    public void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate) {
        this.applySubChunkUpdate(blockList, predicate, true);
    }

    public void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate, boolean queueSave) {
        this.applySubChunkUpdate(blockList, predicate, queueSave, false);
    }

    private void applySubChunkUpdate(List<Block> blockList, Predicate<Block> predicate, boolean queueSave,
                                     boolean structurePlacement) {
        if (predicate != null) {
            ArrayList<Block> filtered = new ArrayList<>(blockList.size());
            for (Block block : blockList) {
                if (predicate.test(block)) {
                    filtered.add(block);
                }
            }
            blockList = filtered;
        }
        if (blockList.isEmpty() && ticksAfterApply.size() == 0 && blockEntitiesAfterApply.size() == 0 && neighborsAfterApply.size() == 0) {
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
        for (int[] tick : ticksAfterApply) {
            long chunkHash = Level.chunkHash(tick[0] >> 4, tick[2] >> 4);
            if (retainedChunks.add(chunkHash)) {
                level.retainGenerationTask(chunkHash);
            }
        }
        for (CompoundTag data : blockEntitiesAfterApply) {
            long chunkHash = Level.chunkHash(data.getInt("x") >> 4, data.getInt("z") >> 4);
            if (retainedChunks.add(chunkHash)) {
                level.retainGenerationTask(chunkHash);
            }
        }
        for (int[] update : neighborsAfterApply) {
            long chunkHash = Level.chunkHash(update[0] >> 4, update[2] >> 4);
            if (retainedChunks.add(chunkHash)) level.retainGenerationTask(chunkHash);
        }

        try {
            HashMap<IChunk, ArrayList<Block>> chunks = new HashMap<>(Math.max(16, blockList.size() >> 3));
            Set<Long> deferredChunks = ConcurrentHashMap.newKeySet();
            boolean shouldBroadcast = !level.getPlayers().isEmpty();
            HashMap<SubChunkEntry, UpdateSubChunkBlocksPacket> batchs =
                    shouldBroadcast ? new HashMap<>(Math.max(16, blockList.size() >> 2)) : null;
            HashMap<Long, List<Player>> recipientsByChunk = shouldBroadcast ? new HashMap<>() : null;
            HashMap<SubChunkEntry, ArrayList<Block>> structureRefresh =
                    shouldBroadcast && structurePlacement ? new HashMap<>() : null;

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
                    SubChunkEntry subChunk = new SubChunkEntry(b.getChunkX() << 4, (b.getFloorY() >> 4) << 4, b.getChunkZ() << 4);
                    UpdateSubChunkBlocksPacket batch = batchs.computeIfAbsent(subChunk, s -> {
                        final UpdateSubChunkBlocksPacket packet = new UpdateSubChunkBlocksPacket();
                        packet.setSubChunkBlockPosition(Vector3i.from(s.x, s.y, s.z));
                        return packet;
                    });
                    if (structurePlacement && b.layer == 0 && !b.isAir()) {
                        structureRefresh.computeIfAbsent(subChunk, ignored -> new ArrayList<>()).add(b);
                    }
                    int flags = structurePlacement ? 19 : 0;
                    if (b.layer == 1) {
                        batch.getExtraBlocks().add(new BlockChangeEntry(
                                b.asBlockVector3().toNetwork(),
                                new RuntimeBlockDefinition((int) b.getBlockState().unsignedBlockStateHash()),
                                flags,
                                -1,
                                ActorBlockSyncMessageId.NONE
                        ));
                    } else {
                        batch.getStandardBlocks().add(new BlockChangeEntry(
                                b.asBlockVector3().toNetwork(),
                                new RuntimeBlockDefinition((int) b.getBlockState().unsignedBlockStateHash()),
                                flags,
                                -1,
                                ActorBlockSyncMessageId.NONE
                        ));
                    }
                }
            }

            HashMap<IChunk, List<int[]>> ticksByChunk = new HashMap<>();
            for (int[] tick : ticksAfterApply) {
                IChunk chunk = level.getChunk(tick[0] >> 4, tick[2] >> 4, true);
                chunks.computeIfAbsent(chunk, ignored -> new ArrayList<>());
                ticksByChunk.computeIfAbsent(chunk, ignored -> new ArrayList<>()).add(tick);
            }

            HashMap<IChunk, List<CompoundTag>> blockEntitiesByChunk = new HashMap<>();
            for (CompoundTag data : blockEntitiesAfterApply) {
                IChunk chunk = level.getChunk(data.getInt("x") >> 4, data.getInt("z") >> 4, true);
                chunks.computeIfAbsent(chunk, ignored -> new ArrayList<>());
                blockEntitiesByChunk.computeIfAbsent(chunk, ignored -> new ArrayList<>()).add(data);
            }

            HashMap<IChunk, List<int[]>> neighborsByChunk = new HashMap<>();
            for (int[] update : neighborsAfterApply) {
                IChunk chunk = level.getChunk(update[0] >> 4, update[2] >> 4, true);
                chunks.computeIfAbsent(chunk, ignored -> new ArrayList<>());
                neighborsByChunk.computeIfAbsent(chunk, ignored -> new ArrayList<>()).add(update);
            }

            boolean trackNeighborWrites = neighborsAfterApply.size() != 0;
            Set<Long> affectedPrimaryWrites = trackNeighborWrites ? ConcurrentHashMap.newKeySet() : Set.of();

            chunks.entrySet().parallelStream().forEach(entry -> {
                final var key = entry.getKey();
                final var value = entry.getValue();
                final List<int[]> ticks = ticksByChunk.getOrDefault(key, List.of());
                final List<CompoundTag> blockEntities = blockEntitiesByChunk.getOrDefault(key, List.of());
                final List<int[]> neighbors = neighborsByChunk.getOrDefault(key, List.of());

                if (key.getFinalizationState() == ChunkFinalizationState.NEEDS_INSTATICKING && queuePendingSubChunkUpdates(key, value, ticks, blockEntities, neighbors)) {
                    deferredChunks.add(Level.chunkHash(key.getX(), key.getZ()));

                    if (trackNeighborWrites) {
                        for (Block b : value) {
                            if (b.layer == 0 && level.isYInRange(b.getFloorY())) {
                                affectedPrimaryWrites.add(hashXYZ(b.getFloorX(), b.getFloorY(), b.getFloorZ(), 0));
                            }
                        }
                    }
                    return;
                }

                if (value.size() != 0) {
                    key.batchProcess(unsafeChunk -> {
                        for (Block b : value) {
                            BlockState previous = unsafeChunk.getAndSetBlockState(
                                    b.getFloorX() & 15,
                                    b.getFloorY(),
                                    b.getFloorZ() & 15,
                                    b.getBlockState(),
                                    b.layer
                            );

                            if (trackNeighborWrites && b.layer == 0 && level.isYInRange(b.getFloorY())
                                    && previous != b.getBlockState()) {
                                affectedPrimaryWrites.add(hashXYZ(b.getFloorX(), b.getFloorY(), b.getFloorZ(), 0));
                            }
                        }
                    });
                    if (queueSave) key.setChanged();
                    key.reObfuscateChunk();
                }

                for (int[] tick : ticks) {
                    scheduleTickAfterApply(level, key, tick);
                }
            });

            for (var entry : chunks.entrySet()) {
                IChunk chunk = entry.getKey();
                if (deferredChunks.contains(Level.chunkHash(chunk.getX(), chunk.getZ()))
                        || chunk.getBlockEntities().isEmpty()) continue;

                for (Block block : entry.getValue()) {
                    if (block.layer != 0) continue;

                    BlockEntity existing = chunk.getTile(block.getFloorX() & 15, block.getFloorY(), block.getFloorZ() & 15);
                    if (existing == null || existing.closed) continue;

                    boolean valid;
                    try {
                        valid = existing.isBlockEntityValid();
                    } catch (Exception e) {
                        valid = false;
                    }
                    if (!valid) existing.close();
                }
            }

            applyHooks();

            for (var entry : neighborsByChunk.entrySet()) {
                IChunk chunk = entry.getKey();
                if (deferredChunks.contains(Level.chunkHash(chunk.getX(), chunk.getZ()))) continue;
                for (int[] update : entry.getValue()) {
                    if (!hasAffectedPrimaryWrite(affectedPrimaryWrites, update)) continue;
                    StructureNeighborUpdates.processOrQueue(level, chunk, update);
                }
            }

            if (shouldBroadcast) {
                for (var entry : batchs.entrySet()) {
                    SubChunkEntry subChunk = entry.getKey();
                    long chunkHash = Level.chunkHash(subChunk.x >> 4, subChunk.z >> 4);
                    if (deferredChunks.contains(chunkHash)) {
                        continue;
                    }
                    if (structurePlacement) {
                        ArrayList<Block> solidBlocks = structureRefresh.get(subChunk);
                        if (solidBlocks != null) {
                            ArrayList<BlockChangeEntry> finalChanges = new ArrayList<>(solidBlocks.size());
                            for (Block b : solidBlocks) {
                                int runtimeId = level.getBlockRuntimeId(b.getFloorX(), b.getFloorY(), b.getFloorZ(), 0);
                                if (runtimeId == Integer.MIN_VALUE) continue;
                                finalChanges.add(new BlockChangeEntry(
                                        b.asBlockVector3().toNetwork(),
                                        new RuntimeBlockDefinition(runtimeId),
                                        2, -1, ActorBlockSyncMessageId.NONE));
                            }
                            entry.getValue().getStandardBlocks().addAll(0, finalChanges);
                        }
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

            for (var entry : blockEntitiesByChunk.entrySet()) {
                IChunk chunk = entry.getKey();
                if (deferredChunks.contains(Level.chunkHash(chunk.getX(), chunk.getZ()))) continue;
                for (CompoundTag data : entry.getValue()) {
                    restoreBlockEntityAfterApply(level, chunk, data);
                }
            }

            for (var b : blockList) {
                long chunkHash = Level.chunkHash(b.getChunkX(), b.getChunkZ());
                if (!deferredChunks.contains(chunkHash) && b instanceof BlockEntityHolder<?> holder) {
                    holder.getOrCreateBlockEntity();
                }
            }

            places.clear();
            caches.clear();
            ticksAfterApply.clear();
            blockEntitiesAfterApply.clear();
            neighborsAfterApply.clear();
        } finally {
            retainedChunks.forEach(level::releaseGenerationTask);
        }
    }

    private boolean hasAffectedPrimaryWrite(Set<Long> writes, int[] update) {
        int x = update[0], y = update[1], z = update[2];

        return writes.contains(hashXYZ(x, y, z, 0))
                || writes.contains(hashXYZ(x - 1, y, z, 0))
                || writes.contains(hashXYZ(x + 1, y, z, 0))
                || writes.contains(hashXYZ(x, y, z - 1, 0))
                || writes.contains(hashXYZ(x, y, z + 1, 0));
    }

    private boolean queuePendingSubChunkUpdates(IChunk chunk, List<Block> blocks, List<int[]> ticks,
                                                List<CompoundTag> blockEntities, List<int[]> neighbors) {
        synchronized (chunk) {
            if (chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING) return false;

            CompoundTag extraData = chunk.getExtraData();
            if (blocks.size() != 0) {
                ListTag<IntArrayTag> pending = extraData.containsList(PENDING_SUB_CHUNK_UPDATES, Tag.TAG_Int_Array)
                        ? extraData.getList(PENDING_SUB_CHUNK_UPDATES, IntArrayTag.class)
                        : new ListTag<>(Tag.TAG_Int_Array);
                for (Block block : blocks) {
                    pending.add(new IntArrayTag(new int[]{
                            block.getFloorX(), block.getFloorY(), block.getFloorZ(),
                            block.layer, block.getBlockState().blockStateHash()
                    }));
                }
                extraData.putList(PENDING_SUB_CHUNK_UPDATES, pending);
            }

            if (ticks.size() != 0) {
                ListTag<IntArrayTag> pendingTicks = extraData.containsList(PENDING_AFTER_APPLY_TICKS, Tag.TAG_Int_Array)
                        ? extraData.getList(PENDING_AFTER_APPLY_TICKS, IntArrayTag.class)
                        : new ListTag<>(Tag.TAG_Int_Array);
                for (int[] tick : ticks) {
                    pendingTicks.add(new IntArrayTag(tick.clone()));
                }
                extraData.putList(PENDING_AFTER_APPLY_TICKS, pendingTicks);
            }

            if (blockEntities.size() != 0) {
                ListTag<CompoundTag> pendingEntities =
                        extraData.containsList(PENDING_AFTER_APPLY_BLOCK_ENTITIES, Tag.TAG_Compound)
                                ? extraData.getList(PENDING_AFTER_APPLY_BLOCK_ENTITIES, CompoundTag.class)
                                : new ListTag<>(Tag.TAG_Compound);
                for (CompoundTag data : blockEntities) {
                    pendingEntities.add(data.copy());
                }
                extraData.putList(PENDING_AFTER_APPLY_BLOCK_ENTITIES, pendingEntities);
            }

            StructureNeighborUpdates.defer(chunk, neighbors);
            chunk.setChanged();
            return true;
        }
    }

    public static void applyPendingSubChunkUpdates(Level level, IChunk chunk) {
        if (chunk.getFinalizationState() == ChunkFinalizationState.NEEDS_INSTATICKING) return;

        ListTag<IntArrayTag> pending;
        ListTag<IntArrayTag> pendingTicks;
        ListTag<CompoundTag> pendingEntities;
        synchronized (chunk) {
            CompoundTag extraData = chunk.getExtraData();
            pending = extraData.containsList(PENDING_SUB_CHUNK_UPDATES, Tag.TAG_Int_Array)
                    ? extraData.removeAndGet(PENDING_SUB_CHUNK_UPDATES) : null;
            pendingTicks = extraData.containsList(PENDING_AFTER_APPLY_TICKS, Tag.TAG_Int_Array)
                    ? extraData.removeAndGet(PENDING_AFTER_APPLY_TICKS) : null;
            pendingEntities = extraData.containsList(PENDING_AFTER_APPLY_BLOCK_ENTITIES, Tag.TAG_Compound)
                    ? extraData.removeAndGet(PENDING_AFTER_APPLY_BLOCK_ENTITIES) : null;
            if (pending != null || pendingTicks != null || pendingEntities != null) chunk.setChanged();
        }

        if (pending != null && pending.size() != 0 || pendingEntities != null && pendingEntities.size() != 0) {
            BlockManager pendingBlocks = new BlockManager(level);
            if (pending != null && pending.size() != 0) BlockManager.fromTag(pending, pendingBlocks);
            if (pendingEntities != null) {
                for (CompoundTag data : pendingEntities.getAll()) {
                    pendingBlocks.queueBlockEntityAfterApply(data);
                }
            }
            pendingBlocks.applySubChunkUpdate(pendingBlocks.getBlocks(), null, true);
        }

        if (pendingTicks != null) {
            for (IntArrayTag entry : pendingTicks.getAll()) {
                int[] tick = entry.getData();
                if (tick.length == 4) scheduleTickAfterApply(level, chunk, tick);
            }
        }
    }

    private static void restoreBlockEntityAfterApply(Level level, IChunk chunk, CompoundTag source) {
        int x = source.getInt("x");
        int y = source.getInt("y");
        int z = source.getInt("z");

        if (chunk.getX() != x >> 4 || chunk.getZ() != z >> 4) return;

        BlockState state = chunk.getBlockState(x & 15, y, z & 15, 0);
        if (state == null) return;

        Block block = Block.get(state, level, x, y, z, 0);
        if (!(block instanceof BlockEntityHolder<?> holder)) return;

        Class<? extends BlockEntity> savedClass = Registries.BLOCKENTITY.get(source.getString("id"));
        if (savedClass == null || !holder.getBlockEntityClass().isAssignableFrom(savedClass)) return;

        BlockEntity existing = level.getBlockEntity(new Vector3(x, y, z));
        if (existing != null && !existing.closed) existing.close();

        CompoundTag nbt = source.copy();
        nbt.putString("id", holder.getBlockEntityType());
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);

        BlockEntity.createBlockEntity(holder.getBlockEntityType(), new Position(x, y, z, level), nbt);
    }

    private static void scheduleTickAfterApply(Level level, IChunk chunk, int[] tick) {
        int x = tick[0], y = tick[1], z = tick[2];
        int localX = x & 15, localZ = z & 15;

        BlockState state = chunk.getBlockState(localX, y, localZ, 0);
        int layer = 0;
        if (state == null || state.getIdentifier().equals(BlockAir.STATE.getIdentifier())) {
            state = chunk.getBlockState(localX, y, localZ, 1);
            layer = 1;
        }
        if (state == null || state.getIdentifier().equals(BlockAir.STATE.getIdentifier())) return;

        Block block = Block.get(state, level, x, y, z, layer);
        chunk.getBlockUpdateScheduler().add(block, new Vector3(x, y, z),
                level.getCurrentTick() + (long) Math.max(0, tick[3]), 0, true);
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
