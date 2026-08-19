package org.powernukkitx.level.generator.object;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockEntityHolder;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
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
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.ActorBlockSyncMessageId;
import org.cloudburstmc.protocol.bedrock.data.BlockChangeEntry;
import org.cloudburstmc.protocol.bedrock.packet.UpdateSubChunkBlocksPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Slf4j
public class BlockManager {
    private static final String PENDING_SUB_CHUNK_UPDATES = "pendingSubChunkUpdates";

    private static final int MAX_PENDING_HOOK_CHUNKS = 4096;

    private static final LinkedHashMap<PendingHookKey, List<Runnable>> PENDING_HOOKS = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<PendingHookKey, List<Runnable>> eldest) {
            if (size() <= MAX_PENDING_HOOK_CHUNKS) {
                return false;
            }
            PendingHookKey key = eldest.getKey();
            log.warn("Discarding {} structure hook(s) waiting on chunk ({}, {}) of level {}: more than {} chunks are "
                            + "waiting to be generated, so what those hooks were meant to populate stays empty",
                    eldest.getValue().size(), Level.getHashX(key.chunkHash()), Level.getHashZ(key.chunkHash()),
                    key.levelId(), MAX_PENDING_HOOK_CHUNKS);
            return true;
        }
    };

    private record PendingHookKey(int levelId, long chunkHash) {
    }

    private final Level level;
    private final Long2ObjectOpenHashMap<Block> caches;
    private final Long2ObjectOpenHashMap<Block> places;

    protected final ObjectOpenHashSet<Runnable> hooks;

    private final Long2ObjectOpenHashMap<ObjectArrayList<Runnable>> chunkHooks;

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
        this.chunkHooks = new Long2ObjectOpenHashMap<>();
    }

    public void addHook(Runnable runnable) {
        this.hooks.add(runnable);
    }

    /**
     * Registers a hook that must not run before the given chunk is generated.
     * <p>
     * A structure is generated in one go for all of the chunks it covers, so it always spills into
     * neighbours that are not generated yet. The blocks meant for those chunks are held until they
     * are, and a hook bound to such a chunk waits with them instead of running on empty terrain.
     *
     * @param chunkX   x coordinate of the chunk the hook depends on
     * @param chunkZ   z coordinate of the chunk the hook depends on
     * @param runnable what to run once that chunk is generated
     */
    public void addHook(int chunkX, int chunkZ, Runnable runnable) {
        this.chunkHooks.computeIfAbsent(Level.chunkHash(chunkX, chunkZ), k -> new ObjectArrayList<>()).add(runnable);
    }

    /**
     * Registers a hook that must not run before the chunk holding the given block is generated.
     *
     * @param block    a block of the chunk the hook depends on
     * @param runnable what to run once that chunk is generated
     * @see #addHook(int, int, Runnable)
     */
    public void addHook(Block block, Runnable runnable) {
        this.addHook(block.getChunkX(), block.getChunkZ(), runnable);
    }

    /**
     * Registers a hook that must not run before the chunk holding the given position is generated.
     *
     * @param pos      a position of the chunk the hook depends on
     * @param runnable what to run once that chunk is generated
     * @see #addHook(int, int, Runnable)
     */
    public void addHook(BlockVector3 pos, Runnable runnable) {
        this.addHook(pos.getX() >> 4, pos.getZ() >> 4, runnable);
    }

    /**
     * Registers a hook that must not run before every one of the given chunks is generated.
     * <p>
     * Used by the structures whose hook populates more than one chunk at a time, so that it never
     * sees half of what it has to fill. This is deliberately all or nothing: if one of the chunks
     * is never generated, the hook never runs at all, rather than running on a partial structure.
     * <p>
     * An empty collection means the hook depends on no chunk and runs immediately, as
     * {@link #addHook(Runnable)}.
     *
     * @param chunkHashes hashes of the chunks the hook depends on, as {@link Level#chunkHash(int, int)}
     * @param runnable    what to run once the last of those chunks is generated
     */
    public void addHook(LongCollection chunkHashes, Runnable runnable) {
        LongOpenHashSet distinct = new LongOpenHashSet(chunkHashes);
        if (distinct.isEmpty()) {
            this.addHook(runnable);
            return;
        }
        AtomicInteger remaining = new AtomicInteger(distinct.size());
        for (long chunkHash : distinct) {
            this.chunkHooks.computeIfAbsent(chunkHash, k -> new ObjectArrayList<>()).add(() -> {
                if (remaining.decrementAndGet() == 0) {
                    runnable.run();
                }
            });
        }
    }

    /**
     * Collects the hashes of the chunks the given positions fall in.
     *
     * @param positions the positions to map to their chunk
     * @return the distinct chunk hashes, ready for {@link #addHook(LongCollection, Runnable)}
     */
    public static LongOpenHashSet chunkHashesOfPositions(Collection<BlockVector3> positions) {
        LongOpenHashSet hashes = new LongOpenHashSet();
        for (BlockVector3 pos : positions) {
            hashes.add(Level.chunkHash(pos.getX() >> 4, pos.getZ() >> 4));
        }
        return hashes;
    }

    /**
     * Collects the hashes of the chunks the given blocks fall in.
     *
     * @param blocks the blocks to map to their chunk
     * @return the distinct chunk hashes, ready for {@link #addHook(LongCollection, Runnable)}
     */
    public static LongOpenHashSet chunkHashesOfBlocks(Collection<Block> blocks) {
        LongOpenHashSet hashes = new LongOpenHashSet();
        for (Block block : blocks) {
            hashes.add(Level.chunkHash(block.getChunkX(), block.getChunkZ()));
        }
        return hashes;
    }

    public ObjectOpenHashSet<Runnable> getHooks() {
        return this.hooks;
    }

    protected void applyHooks() {
        for (var entry : this.chunkHooks.long2ObjectEntrySet()) {
            long chunkHash = entry.getLongKey();
            IChunk chunk = this.level.getChunkIfLoaded(Level.getHashX(chunkHash), Level.getHashZ(chunkHash));
            if (chunk != null && chunk.isGenerated()) {
                this.hooks.addAll(entry.getValue());
            } else {
                deferHooks(this.level, chunkHash, entry.getValue());
            }
        }
        this.chunkHooks.clear();

        hooks.parallelStream().forEach(Runnable::run);
        hooks.clear();
    }

    private static void deferHooks(Level level, long chunkHash, List<Runnable> runnables) {
        PendingHookKey key = new PendingHookKey(level.getId(), chunkHash);
        synchronized (PENDING_HOOKS) {
            PENDING_HOOKS.computeIfAbsent(key, k -> new ObjectArrayList<>()).addAll(runnables);
        }
    }

    private static void runPendingHooks(Level level, IChunk chunk) {
        PendingHookKey key = new PendingHookKey(level.getId(), Level.chunkHash(chunk.getX(), chunk.getZ()));
        List<Runnable> runnables;
        synchronized (PENDING_HOOKS) {
            runnables = PENDING_HOOKS.remove(key);
        }
        if (runnables == null) {
            return;
        }
        for (Runnable runnable : runnables) {
            runnable.run();
        }
    }

    public String getBlockIdIfCachedOrLoaded(int x, int y, int z) {
        return getBlockIfCachedOrLoaded(x, y, z).getId();
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
            this.mergeChunkHooks(manager);
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
        this.mergeChunkHooks(manager);
    }

    private void mergeChunkHooks(BlockManager manager) {
        for (var entry : manager.chunkHooks.long2ObjectEntrySet()) {
            this.chunkHooks.computeIfAbsent(entry.getLongKey(), k -> new ObjectArrayList<>()).addAll(entry.getValue());
        }
    }

    /**
     * Takes over the hooks of another manager, both the unpositioned ones and those waiting on a
     * chunk, without touching its blocks.
     *
     * @param manager the manager whose hooks are taken over
     */
    public void mergeHooks(BlockManager manager) {
        this.hooks.addAll(manager.getHooks());
        this.mergeChunkHooks(manager);
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

    @Deprecated(forRemoval = true)
    public void generateChunks() {
        for (Block block : this.getBlocks()) {
            if (!block.getChunk().isGenerated()) {
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
        HashMap<IChunk, ArrayList<Block>> chunks = new HashMap<>(Math.max(16, blockList.size() >> 3));
        boolean shouldBroadcast = !level.getPlayers().isEmpty();
        HashMap<SubChunkEntry, UpdateSubChunkBlocksPacket> batchs = shouldBroadcast ? new HashMap<>(Math.max(16, blockList.size() >> 2)) : null;
        HashMap<Long, List<Player>> recipientsByChunk = shouldBroadcast ? new HashMap<>() : null;
        for (var b : blockList) {
            ArrayList<Block> chunk = chunks.computeIfAbsent(level.getChunk(b.getChunkX(), b.getChunkZ(), true), c -> new ArrayList<>());
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
                UpdateSubChunkBlocksPacket batch = batchs.computeIfAbsent(new SubChunkEntry(b.getChunkX() << 4, (b.getFloorY() >> 4) << 4, b.getChunkZ() << 4), s -> {
                    final UpdateSubChunkBlocksPacket packet = new UpdateSubChunkBlocksPacket();
                    packet.setChunkX(s.x);
                    packet.setChunkY(s.y);
                    packet.setChunkZ(s.z);
                    return packet;
                });
                if (b.layer == 1) {
                    batch.getExtraBlocks().add(new BlockChangeEntry(b.asBlockVector3().toNetwork(), new RuntimeBlockDefinition((int) b.getBlockState().unsignedBlockStateHash()), 0, -1, ActorBlockSyncMessageId.NONE));
                } else {
                    batch.getStandardBlocks().add(new BlockChangeEntry(b.asBlockVector3().toNetwork(), new RuntimeBlockDefinition((int) b.getBlockState().unsignedBlockStateHash()), 0, -1, ActorBlockSyncMessageId.NONE));
                }
            }
        }
        chunks.entrySet().parallelStream().forEach(entry -> {
            final var key = entry.getKey();
            final var value = entry.getValue();

            if (!key.isGenerated()) {
                queuePendingSubChunkUpdates(key, value);
                return;
            }
            key.batchProcess(unsafeChunk -> {
                int[] highestNewColumnY = new int[16 * 16];
                boolean[] touchedColumn = new boolean[16 * 16];
                int[] touchedColumnIndexes = new int[16 * 16];
                int touchedCount = 0;
                for (Block b : value) {
                    int localX = b.getFloorX() & 15;
                    int localZ = b.getFloorZ() & 15;
                    int floorY = b.getFloorY();
                    unsafeChunk.setBlockState(localX, floorY, localZ, b.getBlockState(), b.layer);
                    if (b.layer == 0 && !b.isAir()) {
                        int columnIndex = (localZ << 4) | localX;
                        if (!touchedColumn[columnIndex]) {
                            touchedColumn[columnIndex] = true;
                            touchedColumnIndexes[touchedCount++] = columnIndex;
                            highestNewColumnY[columnIndex] = floorY;
                        } else if (floorY > highestNewColumnY[columnIndex]) {
                            highestNewColumnY[columnIndex] = floorY;
                        }
                    }
                }
                for (int i = 0; i < touchedCount; i++) {
                    int columnIndex = touchedColumnIndexes[i];
                    int localX = columnIndex & 15;
                    int localZ = columnIndex >> 4;
                    int highestNewY = highestNewColumnY[columnIndex];
                    if (highestNewY > unsafeChunk.getHeightMap(localX, localZ)) {
                        unsafeChunk.setHeightMap(localX, localZ, highestNewY);
                    }
                }
            });
            if (queueSave) {
                key.setChanged();
            }
            key.reObfuscateChunk();
        });

        applyHooks();
        for (var b : blockList) {
            if (b instanceof BlockEntityHolder<?> holder) {
                holder.getOrCreateBlockEntity();
            }
        }

        if (shouldBroadcast) {
            for (var entry : batchs.entrySet()) {
                SubChunkEntry subChunk = entry.getKey();
                List<Player> recipients = recipientsByChunk.get(Level.chunkHash(subChunk.x >> 4, subChunk.z >> 4));
                if (recipients == null) {
                    continue;
                }
                long chunkHash = Level.chunkHash(subChunk.x >> 4, subChunk.z >> 4);
                for (Player player : recipients) {
                    if (player.isConnected() && player.getPlayerChunkManager().isSentChunk(chunkHash)) {
                        player.sendPacket(entry.getValue());
                    }
                }
            }
        }
        places.clear();
        caches.clear();
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

    /**
     * Drops every hook still waiting on a chunk of the given level.
     * <p>
     * Called when a level is unloaded: those hooks can no longer run, and a level id is reused, so
     * keeping them would eventually fire the work of an unloaded world against a different one.
     *
     * @param levelId the id of the level being unloaded, as {@link Level#getId()}
     */
    public static void clearPendingHooks(int levelId) {
        synchronized (PENDING_HOOKS) {
            PENDING_HOOKS.keySet().removeIf(key -> key.levelId() == levelId);
        }
    }

    public static void applyPendingSubChunkUpdates(Level level, IChunk chunk) {
        if (!chunk.isGenerated()) {
            return;
        }

        applyPendingBlocks(level, chunk);
        runPendingHooks(level, chunk);
    }

    private static void applyPendingBlocks(Level level, IChunk chunk) {
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
