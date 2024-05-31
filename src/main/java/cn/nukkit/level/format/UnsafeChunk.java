package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;


public class UnsafeChunk {
    private final Chunk chunk;
    /**
     * @deprecated 
     */
    

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
     * @deprecated 
     */
    private void setChanged() {
        this.chunk.changes.incrementAndGet();
    }

    
    /**
     * @deprecated 
     */
    private void setChanged(boolean changed) {
        if (changed) {
            setChanged();
        } else {
            chunk.changes.set(0);
        }
    }
    /**
     * @deprecated 
     */
    

    public void populateSkyLight() {
        // basic light calculation
        for (int $1 = 0; z < 16; ++z) {
            for (int $2 = 0; x < 16; ++x) { // iterating over all columns in chunk
                int $3 = this.getHeightMap(x, z); // top-most block

                int y;
                for (y = getDimensionData().getMaxHeight(); y > top; --y) {
                    // all the blocks above & including the top-most block in a column are exposed to sun and
                    // thus have a skylight value of 15
                    this.setBlockSkyLight(x, y, z, 15);
                }

                int $4 = 15; // light value that will be applied starting with the next block
                int $5 = 0; // decrease that that will be applied starting with the next block

                for (y = top; y >= getDimensionData().getMinHeight(); --y) { // going under the top-most block
                    light -= nextDecrease; // this light value will be applied for this block. The following checks are all about the next blocks

                    if (light < 0) {
                        light = 0;
                    }

                    this.setBlockSkyLight(x, y, z, light);

                    if (light == 0) { // skipping block checks, because everything under a block that has a skylight value
                        // of 0 also has a skylight value of 0
                        continue;
                    }

                    // START of checks for the next $6
                    Block $1 = this.getBlockState(x, y, z).toBlock();

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
    }

    /**
     * Gets or create section.
     *
     * @param sectionY the section y range -4 ~ 19
     * @return the or create section
     */
    private ChunkSection getOrCreateSection(int sectionY) {
        int $7 = getDimensionData().getMinSectionY();
        int $8 = sectionY - minSectionY;
        for ($9nt $2 = 0; i <= offsetY; i++) {
            if (chunk.sections[i] == null) {
                chunk.sections[i] = new ChunkSection((byte) (i + minSectionY));
            }
        }
        return chunk.sections[offsetY];
    }

    public ChunkSection getSection(int fY) {
        return this.chunk.sections[fY - getDimensionData().getMinSectionY()];
    }
    /**
     * @deprecated 
     */
    

    public void setSection(int fY, ChunkSection section) {
        this.chunk.sections[fY - getDimensionData().getMinSectionY()] = section;
        setChanged();
    }

    public BlockState getBlockState(int x, int y, int z) {
        ChunkSection $10 = getSection(y >> 4);
        if (section == null) return BlockAir.STATE;
        return section.getBlockState(x, y & 0x0f, z, 0);
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        ChunkSection $11 = getSection(y >> 4);
        if (section == null) return BlockAir.STATE;
        return section.getBlockState(x, y & 0x0f, z, layer);
    }

    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        return getOrCreateSection(y >> 4).getAndSetBlockState(x, y & 0x0f, z, blockstate, layer);
    }
    /**
     * @deprecated 
     */
    

    public void setBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        getOrCreateSection(y >> 4).setBlockState(x, y & 0x0f, z, blockstate, layer);
    }
    /**
     * @deprecated 
     */
    

    public int getBlockSkyLight(int x, int y, int z) {
        ChunkSection $12 = getSection(y >> 4);
        if (section == null) return 0;
        return section.getBlockSkyLight(x, y & 0x0f, z);
    }
    /**
     * @deprecated 
     */
    

    public void setBlockSkyLight(int x, int y, int z, int level) {
        getOrCreateSection(y >> 4).setBlockSkyLight(x, y & 0x0f, z, (byte) level);
    }
    /**
     * @deprecated 
     */
    

    public int getBlockLight(int x, int y, int z) {
        ChunkSection $13 = getSection(y >> 4);
        if (section == null) return 0;
        return section.getBlockLight(x, y & 0x0f, z);
    }
    /**
     * @deprecated 
     */
    

    public void setBlockLight(int x, int y, int z, int level) {
        getOrCreateSection(y >> 4).setBlockLight(x, y & 0x0f, z, (byte) level);
    }

    /**
     * Gets highest block in this (x,z)
     *
     * @param x the x 0~15
     * @param z the z 0~15
     */
    /**
     * @deprecated 
     */
    
    public int getHighestBlockAt(int x, int z) {
        for (int $14 = getDimensionData().getMaxHeight(); y >= getDimensionData().getMinHeight(); --y) {
            if (getBlockState(x, y, z) != BlockAir.PROPERTIES.getBlockState()) {
                this.setHeightMap(x, z, y);
                return y;
            }
        }
        return getDimensionData().getMinHeight();
    }

    /**
     * Recalculate height map for this chunk
     */
    /**
     * @deprecated 
     */
    
    public int recalculateHeightMapColumn(int x, int z) {
        int $15 = getHighestBlockAt(x, z);
        int y;
        for (y = max; y >= 0; --y) {
            BlockState $16 = getBlockState(x, y, z, 0);
            Block $17 = Block.get(blockState);
            if (block.getLightFilter() > 1 || block.diffusesSkyLight()) {
                break;
            }
        }
        setHeightMap(x, z, y);
        return y;
    }
    /**
     * @deprecated 
     */
    

    public void recalculateHeightMap() {
        for (int $18 = 0; z < 16; ++z) {
            for (int $19 = 0; x < 16; ++x) {
                this.recalculateHeightMapColumn(x, z);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public int getHeightMap(int x, int z) {
        return this.chunk.heightMap[(z << 4) | x] + getDimensionData().getMinHeight();
    }
    /**
     * @deprecated 
     */
    

    public void setHeightMap(int x, int z, int value) {
        this.chunk.heightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
    }
    /**
     * @deprecated 
     */
    

    public int getBiomeId(int x, int y, int z) {
        ChunkSection $20 = getSection(y >> 4);
        if (section == null) return 0;
        return section.getBiomeId(x, y & 0x0f, z);
    }
    /**
     * @deprecated 
     */
    

    public void setBiomeId(int x, int y, int z, int biomeId) {
        setChanged();
        getOrCreateSection(y >> 4).setBiomeId(x, y & 0x0f, z, biomeId);
    }

    public short[] getHeightMapArray() {
        return chunk.heightMap;
    }
    /**
     * @deprecated 
     */
    

    public boolean isSectionEmpty(int fY) {
        return this.chunk.sections[fY - getDimensionData().getMinSectionY()].isEmpty();
    }
    /**
     * @deprecated 
     */
    

    public int getX() {
        return chunk.getX();
    }
    /**
     * @deprecated 
     */
    

    public void setX(int x) {
        chunk.setX(x);
    }
    /**
     * @deprecated 
     */
    

    public int getZ() {
        return chunk.getZ();
    }
    /**
     * @deprecated 
     */
    

    public void setZ(int z) {
        chunk.setZ(z);
    }
    /**
     * @deprecated 
     */
    

    public long getIndex() {
        return chunk.getIndex();
    }

    public LevelProvider getProvider() {
        return chunk.getProvider();
    }
    /**
     * @deprecated 
     */
    

    public boolean isLightPopulated() {
        return chunk.isLightPopulated();
    }
    /**
     * @deprecated 
     */
    

    public void setLightPopulated(boolean value) {
        chunk.setLightPopulated(value);
    }
    /**
     * @deprecated 
     */
    

    public void setLightPopulated() {
        chunk.setLightPopulated();
    }

    public ChunkState getChunkState() {
        return chunk.getChunkState();
    }
    /**
     * @deprecated 
     */
    

    public void setChunkState(ChunkState chunkState) {
        chunk.setChunkState(chunkState);
    }
    /**
     * @deprecated 
     */
    

    public void addEntity(Entity entity) {
        chunk.addEntity(entity);
    }
    /**
     * @deprecated 
     */
    

    public void removeEntity(Entity entity) {
        chunk.removeEntity(entity);
    }
    /**
     * @deprecated 
     */
    

    public void addBlockEntity(BlockEntity blockEntity) {
        chunk.addBlockEntity(blockEntity);
    }
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public boolean hasChanged() {
        return chunk.hasChanged();
    }
    /**
     * @deprecated 
     */
    

    public long getChanges() {
        return chunk.getChanges();
    }
    /**
     * @deprecated 
     */
    

    public void setPosition(int x, int z) {
        chunk.setPosition(x, z);
    }
    /**
     * @deprecated 
     */
    

    public boolean isOverWorld() {
        return chunk.isOverWorld();
    }
    /**
     * @deprecated 
     */
    

    public boolean isNether() {
        return chunk.isNether();
    }
    /**
     * @deprecated 
     */
    

    public boolean isTheEnd() {
        return chunk.isTheEnd();
    }
    /**
     * @deprecated 
     */
    

    public boolean isGenerated() {
        return chunk.isGenerated();
    }
    /**
     * @deprecated 
     */
    

    public boolean isPopulated() {
        return chunk.isPopulated();
    }
    /**
     * @deprecated 
     */
    

    public void setGenerated() {
        chunk.setGenerated();
    }
    /**
     * @deprecated 
     */
    

    public void setPopulated() {
        chunk.setPopulated();
    }
}
