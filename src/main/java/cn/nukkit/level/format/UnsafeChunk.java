package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;


public class UnsafeChunk {
    private final Chunk chunk;

    public UnsafeChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @ApiStatus.Internal
    public ChunkSection[] getSections() {
        return this.chunk.sections;
    }

    public DimensionData getDimensionData() {
        return chunk.getDimensionData();
    }

    public Map<Long, BlockEntity> getBlockEntities() {
        return this.chunk.tiles;
    }

    /**
     * Gets or create section.
     *
     * @param sectionY the section y range -4 ~ 19
     * @return the or create section
     */
    private ChunkSection getOrCreateSection(int sectionY) {
        int minSectionY = getDimensionData().getMinSectionY();
        int offsetY = sectionY - minSectionY;
        for (int i = 0; i <= offsetY; i++) {
            if (chunk.sections[i] == null) {
                chunk.sections[i] = new ChunkSection((byte) (i + minSectionY));
            }
        }
        return chunk.sections[offsetY];
    }

    private void setChanged() {
        this.chunk.changes.incrementAndGet();
    }

    private void setChanged(boolean changed) {
        if (changed) {
            setChanged();
        } else {
            chunk.changes.set(0);
        }
    }

    public ChunkSection getSection(int fY) {
        return this.chunk.sections[fY - getDimensionData().getMinSectionY()];
    }

    public void setSection(int fY, ChunkSection section) {
        this.chunk.sections[fY - getDimensionData().getMinSectionY()] = section;
        setChanged();
    }

    public BlockState getBlockState(int x, int y, int z) {
        return getSection(y >> 4).getBlockState(x, y & 0x0f, z, 0);
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        return getSection(y >> 4).getBlockState(x, y & 0x0f, z, layer);
    }

    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        return getOrCreateSection(y >> 4).getAndSetBlockState(x, y & 0x0f, z, blockstate, layer);
    }

    public void setBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        getOrCreateSection(y >> 4).setBlockState(x, y & 0x0f, z, blockstate, layer);
    }

    public int getBlockSkyLight(int x, int y, int z) {
        return getSection(y >> 4).getBlockSkyLight(x, y & 0x0f, z);
    }

    public void setBlockSkyLight(int x, int y, int z, int level) {
        getOrCreateSection(y >> 4).setBlockSkyLight(x, y & 0x0f, z, (byte) level);
    }

    public int getBlockLight(int x, int y, int z) {
        return getSection(y >> 4).getBlockLight(x, y & 0x0f, z);
    }

    public void setBlockLight(int x, int y, int z, int level) {
        getOrCreateSection(y >> 4).setBlockLight(x, y & 0x0f, z, (byte) level);
    }

    public int getHighestBlockAt(int x, int z, boolean cache) {
        if (cache) {
            return this.getHeightMap(x, z);
        } else {
            for (int y = getDimensionData().getMaxHeight(); y >= getDimensionData().getMinHeight(); --y) {
                if (getBlockState(x, y, z) != BlockAir.PROPERTIES.getBlockState()) {
                    this.setHeightMap(x, z, y);
                    return y;
                }
            }
            return getDimensionData().getMinHeight();
        }
    }

    public int recalculateHeightMapColumn(int x, int z) {
        int max = getHighestBlockAt(x, z, false);
        int y;
        for (y = max; y >= 0; --y) {
            BlockState blockState = getBlockState(x, y, z, 0);
            Block block = Block.get(blockState);
            if (block.getLightFilter() > 1 || block.diffusesSkyLight()) {
                break;
            }
        }
        setHeightMap(x, z, y + 1);
        return y + 1;
    }

    public int getHeightMap(int x, int z) {
        return this.chunk.heightMap[(z << 4) | x] + getDimensionData().getMinHeight();
    }

    public void setHeightMap(int x, int z, int value) {
        this.chunk.heightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
    }

    public int getBiomeId(int x, int y, int z) {
        return getSection(y >> 4).getBiomeId(x, y & 0x0f, z);
    }

    public void setBiomeId(int x, int y, int z, int biomeId) {
        setChanged();
        getOrCreateSection(y >> 4).setBiomeId(x, y & 0x0f, z, biomeId);
    }

    public short[] getHeightMapArray() {
        return chunk.heightMap;
    }

    public boolean isSectionEmpty(int fY) {
        return this.chunk.sections[fY - getDimensionData().getMinSectionY()].isEmpty();
    }

    public int getX() {
        return chunk.getX();
    }

    public void setX(int x) {
        chunk.setX(x);
    }

    public int getZ() {
        return chunk.getZ();
    }

    public void setZ(int z) {
        chunk.setZ(z);
    }

    public long getIndex() {
        return chunk.getIndex();
    }

    public LevelProvider getProvider() {
        return chunk.getProvider();
    }

    public boolean isLightPopulated() {
        return chunk.isLightPopulated();
    }

    public void setLightPopulated(boolean value) {
        chunk.setLightPopulated(value);
    }

    public void setLightPopulated() {
        chunk.setLightPopulated();
    }

    public ChunkState getChunkState() {
        return chunk.getChunkState();
    }

    public void setChunkState(ChunkState chunkState) {
        chunk.setChunkState(chunkState);
    }

    public void addEntity(Entity entity) {
        chunk.addEntity(entity);
    }

    public void removeEntity(Entity entity) {
        chunk.removeEntity(entity);
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        chunk.addBlockEntity(blockEntity);
    }

    public void removeBlockEntity(BlockEntity blockEntity) {
        chunk.removeBlockEntity(blockEntity);
    }

    public Map<Long, Entity> getEntities() {
        return chunk.getEntities();
    }

    public BlockEntity getTile(int x, int y, int z) {
        return chunk.getTile(x, y, z);
    }

    public CompoundTag getExtraData() {
        return chunk.getExtraData();
    }

    public boolean hasChanged() {
        return chunk.hasChanged();
    }

    public long getChanges() {
        return chunk.getChanges();
    }

    public void setPosition(int x, int z) {
        chunk.setPosition(x, z);
    }

    public boolean isOverWorld() {
        return chunk.isOverWorld();
    }

    public boolean isNether() {
        return chunk.isNether();
    }

    public boolean isTheEnd() {
        return chunk.isTheEnd();
    }

    public boolean isGenerated() {
        return chunk.isGenerated();
    }

    public boolean isPopulated() {
        return chunk.isPopulated();
    }

    public void setGenerated() {
        chunk.setGenerated();
    }

    public void setPopulated() {
        chunk.setPopulated();
    }
}
