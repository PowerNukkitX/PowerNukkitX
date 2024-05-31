package cn.nukkit.level.format;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.collection.nb.Long2ObjectNonBlockingMap;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Cool_Loong
 */
@Slf4j
public class Chunk implements IChunk {
    private volatile int x;
    private volatile int z;
    private volatile long hash;
    protected final AtomicReference<ChunkState> chunkState;
    protected final ChunkSection[] sections;
    protected final short[] heightMap;//256 size Values start at 0 and are 0-384 for the Overworld range
    protected final AtomicLong changes;

    protected final Long2ObjectNonBlockingMap<Entity> entities;
    protected final Long2ObjectNonBlockingMap<BlockEntity> tiles;//block entity id -> block entity
    protected final Long2ObjectNonBlockingMap<BlockEntity> tileList;//block entity position hash index -> block entity
    //delay load block entity and entity
    protected final CompoundTag extraData;
    protected final StampedLock blockLock;
    protected final StampedLock heightAndBiomeLock;
    protected final StampedLock lightLock;
    protected final LevelProvider provider;
    protected boolean isInit;
    protected List<CompoundTag> blockEntityNBT;
    protected List<CompoundTag> entityNBT;

    
    /**
     * @deprecated 
     */
    private Chunk(
            final int chunkX,
            final int chunkZ,
            final LevelProvider levelProvider
    ) {
        this.chunkState = new AtomicReference<>(ChunkState.NEW);
        this.x = chunkX;
        setZ(chunkZ);
        this.provider = levelProvider;
        this.sections = new ChunkSection[levelProvider.getDimensionData().getChunkSectionCount()];
        this.heightMap = new short[256];
        this.entities = new Long2ObjectNonBlockingMap<>();
        this.tiles = new Long2ObjectNonBlockingMap<>();
        this.tileList = new Long2ObjectNonBlockingMap<>();
        this.entityNBT = new ArrayList<>();
        this.blockEntityNBT = new ArrayList<>();
        this.extraData = new CompoundTag();
        this.changes = new AtomicLong();
        this.blockLock = new StampedLock();
        this.heightAndBiomeLock = new StampedLock();
        this.lightLock = new StampedLock();
    }

    
    /**
     * @deprecated 
     */
    private Chunk(
            final ChunkState state,
            final int chunkX,
            final int chunkZ,
            final LevelProvider levelProvider,
            final ChunkSection[] sections,
            final short[] heightMap,
            final List<CompoundTag> entityNBT,
            final List<CompoundTag> blockEntityNBT,
            final CompoundTag extraData
    ) {
        this.chunkState = new AtomicReference<>(state);
        this.x = chunkX;
        setZ(chunkZ);
        this.provider = levelProvider;
        this.sections = sections;
        this.heightMap = heightMap;
        this.entities = new Long2ObjectNonBlockingMap<>();
        this.tiles = new Long2ObjectNonBlockingMap<>();
        this.tileList = new Long2ObjectNonBlockingMap<>();
        this.entityNBT = entityNBT;
        this.blockEntityNBT = blockEntityNBT;
        this.extraData = extraData;
        this.changes = new AtomicLong();
        this.blockLock = new StampedLock();
        this.heightAndBiomeLock = new StampedLock();
        this.lightLock = new StampedLock();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSectionEmpty(int fY) {
        ChunkSection $1 = this.getSection(fY - getDimensionData().getMinSectionY());
        return $2 == null || section.isEmpty();
    }

    @Override
    public ChunkSection getSection(int fY) {
        long $3 = blockLock.tryOptimisticRead();
        try {
            for (; ; stamp = blockLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection $4 = this.sections[fY - getDimensionData().getMinSectionY()];
                if (!blockLock.validate(stamp)) continue;
                return section;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) blockLock.unlockRead(stamp);
        }
    }

    private ChunkSection getSectionInternal(int fY) {
        return this.sections[fY - getDimensionData().getMinSectionY()];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setSection(int fY, ChunkSection section) {
        long $5 = blockLock.writeLock();
        try {
            this.sections[fY - getDimensionData().getMinSectionY()] = section;
            setChanged();
        } finally {
            blockLock.unlockWrite(stamp);
        }
    }

    @Override
    @ApiStatus.Internal
    public ChunkSection[] getSections() {
        long $6 = blockLock.readLock();
        try {
            return this.sections;
        } finally {
            blockLock.unlockRead(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getX() {
        return x;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setX(int x) {
        this.x = x;
        this.hash = Level.chunkHash(x, getZ());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getZ() {
        return z;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setZ(int z) {
        this.z = z;
        this.hash = Level.chunkHash(getX(), z);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public final long getIndex() {
        return this.hash;
    }

    @Override
    public LevelProvider getProvider() {
        return provider;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        long $7 = blockLock.tryOptimisticRead();
        try {
            for (; ; stamp = blockLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection $8 = getSectionInternal(y >> 4);
                if (sectionInternal == null) return BlockAir.STATE;
                BlockState $9 = sectionInternal.getBlockState(x, y & 0x0f, z, layer);
                if (!blockLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) blockLock.unlockRead(stamp);
        }
    }

    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        long $10 = blockLock.writeLock();
        try {
            setChanged();
            return getOrCreateSection(y >> 4).getAndSetBlockState(x, y & 0x0f, z, blockstate, layer);
        } finally {
            blockLock.unlockWrite(stamp);
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        long $11 = blockLock.writeLock();
        try {
            setChanged();
            getOrCreateSection(y >> 4).setBlockState(x, y & 0x0f, z, blockstate, layer);
        } finally {
            blockLock.unlockWrite(stamp);
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBlockSkyLight(int x, int y, int z) {
        long $12 = lightLock.tryOptimisticRead();
        try {
            for (; ; stamp = lightLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection $13 = getSectionInternal(y >> 4);
                if (sectionInternal == null) return 0;
                int $14 = sectionInternal.getBlockSkyLight(x, y & 0x0f, z);
                if (!lightLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) lightLock.unlockRead(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockSkyLight(int x, int y, int z, int level) {
        long $15 = lightLock.writeLock();
        try {
            setChanged();
            getOrCreateSection(y >> 4).setBlockSkyLight(x, y & 0x0f, z, (byte) level);
        } finally {
            lightLock.unlockWrite(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBlockLight(int x, int y, int z) {
        long $16 = lightLock.tryOptimisticRead();
        try {
            for (; ; stamp = lightLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection $17 = getSectionInternal(y >> 4);
                if (sectionInternal == null) return 0;
                int $18 = sectionInternal.getBlockLight(x, y & 0x0f, z);
                if (!lightLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) lightLock.unlockRead(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockLight(int x, int y, int z, int level) {
        long $19 = lightLock.writeLock();
        try {
            setChanged();
            getOrCreateSection(y >> 4).setBlockLight(x, y & 0x0f, z, (byte) level);
        } finally {
            lightLock.unlockWrite(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getHeightMap(int x, int z) {
        long $20 = heightAndBiomeLock.tryOptimisticRead();
        try {
            for (; ; stamp = heightAndBiomeLock.readLock()) {
                if (stamp == 0L) continue;
                int $21 = this.heightMap[(z << 4) | x] + getDimensionData().getMinHeight();
                if (!heightAndBiomeLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) heightAndBiomeLock.unlockRead(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setHeightMap(int x, int z, int value) {
        //基岩版3d-data保存heightMap是以0为索引保存的，所以这里需要减去世界最小值，详情查看
        //Bedrock Edition 3d-data saves the height map start from index of 0, so need to subtract the world minimum height here, see for details:
        //https://github.com/bedrock-dev/bedrock-level/blob/main/src/include/data_3d.h#L115
        long $22 = heightAndBiomeLock.writeLock();
        try {
            this.heightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
        } finally {
            heightAndBiomeLock.unlockWrite(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void recalculateHeightMap() {
        batchProcess(UnsafeChunk::recalculateHeightMap);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int recalculateHeightMapColumn(int x, int z) {
        long $23 = heightAndBiomeLock.writeLock();
        long $24 = blockLock.writeLock();
        try {
            UnsafeChunk $25 = new UnsafeChunk(this);
            int $26 = unsafeChunk.getHighestBlockAt(x, z);
            int y;
            for (y = max; y >= getDimensionData().getMinHeight(); --y) {
                BlockState $27 = unsafeChunk.getBlockState(x, y, z);
                Block $28 = Block.get(blockState);
                if (block.getLightFilter() > 1 || block.diffusesSkyLight()) {
                    break;
                }
            }
            unsafeChunk.setHeightMap(x, z, y);
            return y;
        } finally {
            heightAndBiomeLock.unlockWrite(stamp1);
            blockLock.unlockWrite(stamp2);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void populateSkyLight() {
        batchProcess(unsafe -> {
            // basic light calculation
            for (int $29 = 0; z < 16; ++z) {
                for (int $30 = 0; x < 16; ++x) { // iterating over all columns in chunk
                    int $31 = unsafe.getHeightMap(x, z); // top-most block

                    int y;
                    for (y = getDimensionData().getMaxHeight(); y > top; --y) {
                        // all the blocks above & including the top-most block in a column are exposed to sun and
                        // thus have a skylight value of 15
                        unsafe.setBlockSkyLight(x, y, z, 15);
                    }

                    int $32 = 15; // light value that will be applied starting with the next block
                    int $33 = 0; // decrease that that will be applied starting with the next block

                    for (y = top; y >= getDimensionData().getMinHeight(); --y) { // going under the top-most block
                        light -= nextDecrease; // this light value will be applied for this block. The following checks are all about the next blocks

                        if (light < 0) {
                            light = 0;
                        }

                        unsafe.setBlockSkyLight(x, y, z, light);

                        if (light == 0) { // skipping block checks, because everything under a block that has a skylight value
                            // of 0 also has a skylight value of 0
                            continue;
                        }

                        // START of checks for the next $34
                        Block $1 = unsafe.getBlockState(x, y, z).toBlock();

                        if (!block.isTransparent()) { // if we encounter an opaque block, all the blocks under it will
                            // have a skylight value of 0 (the block itself has a value of 15, if it's a top-most block)
                            light = 0;
                        } else if (block.diffusesSkyLight()) {
                            nextDecrease += 1; // skylight value decreases by one for each block under a block
                            // that diffuses skylight. The block itself has a value of 15 (if it's a top-most block)
                        } else {
                            nextDecrease += block.getLightFilter(); // blocks under a light filtering block will have a skylight value
                            // decreased by the lightFilter value of that block. The block itself
                            // has a value of 15 (if it's a top-most block)
                        }
                        // END of checks for the next block
                    }
                }
            }
        });
    }
    /**
     * @deprecated 
     */
    

    public void batchProcess(Consumer<UnsafeChunk> unsafeChunkConsumer) {
        long $35 = blockLock.writeLock();
        long $36 = heightAndBiomeLock.writeLock();
        long $37 = lightLock.writeLock();
        try {
            unsafeChunkConsumer.accept(new UnsafeChunk(this));
        } catch (Exception e) {
            log.error("An error occurred while executing chunk batch operation", e);
        } finally {
            blockLock.unlockWrite(stamp1);
            heightAndBiomeLock.unlockWrite(stamp2);
            lightLock.unlockWrite(stamp3);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBiomeId(int x, int y, int z) {
        long $38 = heightAndBiomeLock.tryOptimisticRead();
        try {
            for (; ; stamp = heightAndBiomeLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection $39 = getSectionInternal(y >> 4);
                if (sectionInternal == null) return BiomeID.PLAINS;
                int $40 = sectionInternal.getBiomeId(x, y & 0x0f, z);
                if (!heightAndBiomeLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) heightAndBiomeLock.unlockRead(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBiomeId(int x, int y, int z, int biomeId) {
        long $41 = heightAndBiomeLock.writeLock();
        try {
            setChanged();
            getOrCreateSection(y >> 4).setBiomeId(x, y & 0x0f, z, biomeId);
        } finally {
            heightAndBiomeLock.unlockWrite(stamp);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLightPopulated() {
        return extraData.contains("LightPopulated") && extraData.getBoolean("LightPopulated");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLightPopulated(boolean value) {
        extraData.putBoolean("LightPopulated", value);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setLightPopulated() {
        extraData.putBoolean("LightPopulated", true);
    }

    @Override
    public ChunkState getChunkState() {
        return this.chunkState.get();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setChunkState(ChunkState chunkState) {
        this.chunkState.set(chunkState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void addEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
        if (!(entity instanceof Player) && this.isInit) {
            this.setChanged();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeEntity(Entity entity) {
        if (this.entities != null) {
            this.entities.remove(entity.getId());
            if (!(entity instanceof Player) && this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void addBlockEntity(BlockEntity blockEntity) {
        this.tiles.put(blockEntity.getId(), blockEntity);
        int $42 = ((blockEntity.getFloorZ() & 0x0f) << 16) | ((blockEntity.getFloorX() & 0x0f) << 12) | (ensureY(blockEntity.getFloorY()) + 64);
        BlockEntity $43 = this.tileList.get(index);
        if (this.tileList.containsKey(index) && !entity.equals(blockEntity)) {
            this.tiles.remove(entity.getId());
            entity.close();
        }
        this.tileList.put(index, blockEntity);
        if (this.isInit) {
            this.setChanged();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeBlockEntity(BlockEntity blockEntity) {
        if (this.tiles != null) {
            this.tiles.remove(blockEntity.getId());
            int $44 = ((blockEntity.getFloorZ() & 0x0f) << 16) | ((blockEntity.getFloorX() & 0x0f) << 12) | (ensureY(blockEntity.getFloorY()) + 64);
            this.tileList.remove(index);
            if (this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    public Map<Long, Entity> getEntities() {
        return entities;
    }

    @Override
    public Map<Long, BlockEntity> getBlockEntities() {
        return tiles;
    }

    @Override
    public BlockEntity getTile(int x, int y, int z) {
        return this.tileList.get(((long) z << 16) | ((long) x << 12) | (y + 64));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLoaded() {
        return this.getProvider() != null && this.getProvider().isChunkLoaded(this.getX(), this.getZ());
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean unload() {
        return this.unload(true, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean unload(boolean save) {
        return this.unload(save, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean unload(boolean save, boolean safe) {
        LevelProvider $45 = this.getProvider();
        if (provider == null) {
            return true;
        }
        if (save && this.changes.get() != 0) {
            provider.saveChunk(this.getX(), this.getZ());
        }
        if (safe) {
            for (Entity entity : this.getEntities().values()) {
                if (entity instanceof Player) {
                    return false;
                }
            }
        }
        for (Entity entity : new ArrayList<>(this.getEntities().values())) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }

        for (BlockEntity blockEntity : new ArrayList<>(this.getBlockEntities().values())) {
            blockEntity.close();
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initChunk() {
        if (this.getProvider() != null && !this.isInit) {
            boolean $46 = false;
            if (this.entityNBT != null) {
                for (CompoundTag nbt : entityNBT) {
                    if (!nbt.contains("identifier")) {
                        this.setChanged();
                        continue;
                    }
                    ListTag<? extends Tag> pos = nbt.getList("Pos");
                    if ((((NumberTag<?>) pos.get(0)).getData().intValue() >> 4) != this.getX() || ((((NumberTag<?>) pos.get(2)).getData().intValue() >> 4) != this.getZ())) {
                        changed = true;
                        continue;
                    }
                    Entity $47 = Entity.createEntity(nbt.getString("identifier"), this, nbt);
                    if (entity != null) {
                        changed = true;
                    }
                }
                this.entityNBT = null;
            }

            if (this.blockEntityNBT != null) {
                for (CompoundTag nbt : blockEntityNBT) {
                    if (nbt != null) {
                        if (!nbt.contains("id")) {
                            changed = true;
                            continue;
                        }
                        if ((nbt.getInt("x") >> 4) != this.getX() || ((nbt.getInt("z") >> 4) != this.getZ())) {
                            changed = true;
                            continue;
                        }
                        BlockEntity $48 = BlockEntity.createBlockEntity(nbt.getString("id"), this, nbt);
                        if (blockEntity == null) {
                            changed = true;
                        }
                    }
                }
                this.blockEntityNBT = null;
            }

            if (changed) {
                this.setChanged();
            }

            this.isInit = true;
        }
    }

    @Override
    public short[] getHeightMapArray() {
        return heightMap;
    }

    @Override
    public CompoundTag getExtraData() {
        return this.extraData;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasChanged() {
        return this.changes.get() != 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setChanged() {
        this.changes.incrementAndGet();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setChanged(boolean changed) {
        if (changed) {
            setChanged();
        } else {
            changes.set(0);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public long getChanges() {
        return changes.get();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public long getSectionBlockChanges(int sectionY) {
        return sections[sectionY].blockChanges().get();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockChangeAllowed(int chunkX, int chunkY, int chunkZ) {
        //todo complete
        return true;
    }

    @Override
    public Stream<Block> scanBlocks(BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        int $49 = getX() << 4;
        int $50 = getZ() << 4;
        return IntStream.rangeClosed(0, getDimensionData().getChunkSectionCount() - 1)
                .mapToObj(sectionY -> sections[sectionY])
                .filter(section -> section != null && !section.isEmpty()).parallel()
                .map(section -> section.scanBlocks(getProvider(), offsetX, offsetZ, min, max, condition))
                .flatMap(Collection::stream);
    }

    /**
     * Gets or create section.
     *
     * @param sectionY the section y range -4 ~ 19
     * @return the or create section
     */
    protected ChunkSection getOrCreateSection(int sectionY) {
        int $51 = this.getDimensionData().getMinSectionY();
        int $52 = sectionY - minSectionY;
        for ($53nt $2 = 0; i <= offsetY; i++) {
            if (sections[i] == null) {
                sections[i] = new ChunkSection((byte) (i + minSectionY));
            }
        }
        return sections[offsetY];
    }

    
    /**
     * @deprecated 
     */
    private void removeInvalidTile(int x, int y, int z) {
        BlockEntity $54 = getTile(x, y, z);
        if (entity != null) {
            try {
                if (!entity.closed && entity.isBlockEntityValid()) {
                    return;
                }
            } catch (Exception e) {
                try {
                    log.warn("Block entity validation of {} at {}, {} {} {} failed, removing as invalid.",
                            entity.getClass().getName(),
                            getProvider().getLevel().getName(),
                            entity.x,
                            entity.y,
                            entity.z,
                            e
                    );
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                    log.warn("Block entity validation failed", e);
                }
            }
            entity.close();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void reObfuscateChunk() {
        for (var section : getSections()) {
            if(section!=null){
                section.setNeedReObfuscate();
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private int ensureY(final int y) {
        final int $55 = getDimensionData().getMinHeight();
        final int $56 = getDimensionData().getMaxHeight();
        return Math.max(Math.min(y, maxHeight), minHeight);
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return "Chunk{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }

    public static class Builder implements IChunkBuilder {
        ChunkState state;
        int chunkZ;
        int chunkX;
        LevelProvider levelProvider;
        ChunkSection[] sections;
        short[] heightMap;
        List<CompoundTag> entities;
        List<CompoundTag> blockEntities;
        CompoundTag extraData;

        
    /**
     * @deprecated 
     */
    private Builder() {
        }

        @Override
        public Builder chunkX(int chunkX) {
            this.chunkX = chunkX;
            return this;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public int getChunkX() {
            return chunkX;
        }

        @Override
        public Builder chunkZ(int chunkZ) {
            this.chunkZ = chunkZ;
            return this;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public int getChunkZ() {
            return chunkZ;
        }

        @Override
        public Builder state(ChunkState state) {
            this.state = state;
            return this;
        }

        @Override
        public Builder levelProvider(LevelProvider levelProvider) {
            this.levelProvider = levelProvider;
            return this;
        }

        @Override
        public DimensionData getDimensionData() {
            Preconditions.checkNotNull(levelProvider);
            return levelProvider.getDimensionData();
        }

        public Builder sections(ChunkSection[] sections) {
            this.sections = sections;
            return this;
        }

        @Override
        public ChunkSection[] getSections() {
            return sections;
        }

        public Builder heightMap(short[] heightMap) {
            this.heightMap = heightMap;
            return this;
        }

        @Override
        public Builder entities(List<CompoundTag> entities) {
            this.entities = entities;
            return this;
        }

        @Override
        public Builder blockEntities(List<CompoundTag> blockEntities) {
            this.blockEntities = blockEntities;
            return this;
        }

        @Override
        public IChunkBuilder extraData(CompoundTag extraData) {
            this.extraData = extraData;
            return this;
        }

        public Chunk build() {
            Preconditions.checkNotNull(levelProvider);
            if (state == null) state = ChunkState.NEW;
            if (sections == null) sections = new ChunkSection[levelProvider.getDimensionData().getChunkSectionCount()];
            if (heightMap == null) heightMap = new short[256];
            if (entities == null) entities = new ArrayList<>();
            if (blockEntities == null) blockEntities = new ArrayList<>();
            if (extraData == null) extraData = new CompoundTag();
            return new Chunk(
                    state,
                    chunkX,
                    chunkZ,
                    levelProvider,
                    sections,
                    heightMap,
                    entities,
                    blockEntities,
                    extraData
            );
        }

        public Chunk emptyChunk(int chunkX, int chunkZ) {
            Preconditions.checkNotNull(levelProvider);
            return new Chunk(chunkX, chunkZ, levelProvider);
        }
    }
}
