package cn.nukkit.level.newformat;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.collection.nb.Long2ObjectNonBlockingMap;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public class Chunk implements IChunk {
    protected final Map<Long, Entity> entities;
    protected final Map<Long, BlockEntity> blockEntities;
    //delay load block entity and entity
    protected List<CompoundTag> blockEntityNBT;
    protected List<CompoundTag> entityNBT;
    protected final LevelProvider provider;
    protected final ChunkSection[] sections;
    protected final short[] heightMap;//256 size
    protected ChunkState chunkState;
    protected long changes;
    private int x;
    private int z;
    private long hash;

    private Chunk(
            final int chunkX,
            final int chunkZ,
            final cn.nukkit.level.newformat.LevelProvider levelProvider
    ) {
        this.chunkState = ChunkState.NEW;
        this.x = chunkX;
        this.z = chunkZ;
        this.provider = levelProvider;
        this.sections = new ChunkSection[levelProvider.getDimensionData().getChunkSectionCount()];
        this.heightMap = new short[256];
        this.entities = new Long2ObjectNonBlockingMap<>();
        this.blockEntities = new Long2ObjectNonBlockingMap<>();
        this.entityNBT = new ArrayList<>();
        this.blockEntityNBT = new ArrayList<>();
    }

    private Chunk(
            final ChunkState state,
            final int chunkX,
            final int chunkZ,
            final cn.nukkit.level.newformat.LevelProvider levelProvider,
            final ChunkSection[] sections,
            final short[] heightMap,
            final Map<Long, Entity> entities,
            final Map<Long, BlockEntity> blockEntities,
            final List<CompoundTag> entityNBT,
            final List<CompoundTag> blockEntityNBT
    ) {
        this.chunkState = state;
        this.x = chunkX;
        this.z = chunkZ;
        this.provider = levelProvider;
        this.sections = sections;
        this.heightMap = heightMap;
        this.entities = entities;
        this.blockEntities = blockEntities;
        this.entityNBT = entityNBT;
        this.blockEntityNBT = blockEntityNBT;
    }

    @Override
    public boolean isSectionEmpty(int fY) {
        return false;
    }

    @Override
    public ChunkSection getSection(int fY) {
        return null;
    }

    @Override
    public boolean setSection(int fY, ChunkSection section) {
        return false;
    }

    @Override
    public ChunkSection[] getSections() {
        return new ChunkSection[0];
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public int getZ() {
        return 0;
    }

    @Override
    public void setZ(int z) {

    }

    @Override
    public long getIndex() {
        return 0;
    }

    @Override
    public LevelProvider getProvider() {
        return null;
    }

    @Override
    public void setProvider(LevelProvider provider) {

    }

    @Override
    public Block getBlock(int x, int y, int z, int layer) {
        return null;
    }

    @Override
    public Block getAndSetBlock(Block block, int layer) {
        return null;
    }

    @Override
    public boolean setBlock(Block block) {
        return false;
    }

    @Override
    public boolean setBlock(Block block, int layer) {
        return false;
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {

    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {

    }

    @Override
    public int getHighestBlockAt(int x, int z) {
        return 0;
    }

    @Override
    public int getHighestBlockAt(int x, int z, boolean cache) {
        return 0;
    }

    @Override
    public int getHeightMap(int x, int z) {
        return 0;
    }

    @Override
    public void setHeightMap(int x, int z, int value) {

    }

    @Override
    public void recalculateHeightMap() {

    }

    @Override
    public int recalculateHeightMapColumn(int chunkX, int chunkZ) {
        return 0;
    }

    @Override
    public void populateSkyLight() {

    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBiomeId(int x, int y, int z, int biomeId) {

    }

    @Override
    public Biome getBiome(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBiome(int x, int y, int z, Biome biome) {

    }

    @Override
    public boolean isLightPopulated() {
        return false;
    }

    @Override
    public void setLightPopulated(boolean value) {

    }

    @Override
    public void setLightPopulated() {

    }

    @Override
    public boolean isPopulated() {
        return false;
    }

    @Override
    public void setPopulated(boolean value) {

    }

    @Override
    public void setPopulated() {

    }

    @Override
    public boolean isGenerated() {
        return false;
    }

    @Override
    public void setGenerated(boolean value) {

    }

    @Override
    public void setGenerated() {

    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public void removeEntity(Entity entity) {

    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {

    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {

    }

    @Override
    public Map<Long, Entity> getEntities() {
        return null;
    }

    @Override
    public Map<Long, BlockEntity> getBlockEntities() {
        return null;
    }

    @Override
    public BlockEntity getTile(int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean load() throws IOException {
        return false;
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return false;
    }

    @Override
    public boolean unload() throws Exception {
        return false;
    }

    @Override
    public boolean unload(boolean save) throws Exception {
        return false;
    }

    @Override
    public boolean unload(boolean save, boolean safe) throws Exception {
        return false;
    }

    @Override
    public void initChunk() {

    }

    @Override
    public byte[] getBiomeIdArray() {
        return new byte[0];
    }

    @Override
    public short[] getHeightMapArray() {
        return new short[0];
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        return new byte[0];
    }

    @Override
    public byte[] getBlockLightArray() {
        return new byte[0];
    }

    @Override
    public boolean hasChanged() {
        return false;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public void setChanged(boolean changed) {

    }

    @Override
    public long getBlockChanges() {
        return 0;
    }

    @Override
    public boolean isBlockChangeAllowed(int x, int y, int z) {
        return false;
    }

    public static class Builder implements IChunkBuilder {
        ChunkState state;
        int chunkZ;
        int chunkX;
        cn.nukkit.level.newformat.LevelProvider levelProvider;
        ChunkSection[] sections;
        short[] heightMap;
        List<CompoundTag> entities;
        List<CompoundTag> blockEntities;
        List<CompoundTag> blockEntityNBT;
        List<CompoundTag> entityNBT;

        public Builder chunkX(int chunkX) {
            this.chunkX = chunkX;
            return this;
        }

        @Override
        public int getChunkX() {
            return chunkX;
        }

        public Builder chunkZ(int chunkZ) {
            this.chunkZ = chunkZ;
            return this;
        }

        @Override
        public int getChunkZ() {
            return chunkZ;
        }

        public Builder state(ChunkState state) {
            this.state = state;
            return this;
        }

        public Builder levelProvider(cn.nukkit.level.newformat.LevelProvider levelProvider) {
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

        public Builder entities(List<CompoundTag> entities) {
            this.entities = entities;
            return this;
        }

        public Builder blockEntities(List<CompoundTag> blockEntities) {
            this.blockEntities = blockEntities;
            return this;
        }

        public Chunk build() {
            Preconditions.checkNotNull(levelProvider);
            if (state == null) state = ChunkState.NEW;
            if (sections == null) sections = new ChunkSection[levelProvider.getDimensionData().getChunkSectionCount()];
            if (heightMap == null) heightMap = new short[256];
            if (entities == null) entities = new ArrayList<>();
            if (blockEntities == null) blockEntities = new ArrayList<>();
            return new Chunk(
                    state,
                    chunkX,
                    chunkZ,
                    levelProvider,
                    sections,
                    heightMap,
                    new Long2ObjectNonBlockingMap<>(),
                    new Long2ObjectNonBlockingMap<>(),
                    entities,
                    blockEntities
            );
        }

        public Chunk emptyChunk(int chunkX, int chunkZ) {
            Preconditions.checkNotNull(levelProvider);
            return new Chunk(chunkX, chunkZ, levelProvider);
        }
    }
}
