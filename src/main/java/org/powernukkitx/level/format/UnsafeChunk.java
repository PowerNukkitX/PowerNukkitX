package org.powernukkitx.level.format;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.generator.ChunkGenerationState;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;


public class UnsafeChunk {
    private final Chunk chunk;

    public UnsafeChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    @ApiStatus.Internal
    public Chunk getChunk() {
        return chunk;
    }

    @ApiStatus.Internal
    public ChunkSection[] getSections() {
        return this.chunk.sections;
    }

    /**
     * Returns the chunk biome palettes without acquiring chunk locks.
     *
     * @return biome section palettes
     */
    @ApiStatus.Internal
    public Palette<Integer>[] getBiomeSections() {
        return this.chunk.biomeSections;
    }

    public DimensionData getDimensionData() {
        return chunk.getDimensionData();
    }

    public Map<Long, BlockEntity> getBlockEntities() {
        return this.chunk.tiles;
    }

    private void setChanged() {
        this.chunk.changes.incrementAndGet();
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void populateSkyLight() {
        // basic light calculation
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) { // iterating over all columns in chunk
                int top = this.getHeightMap(x, z) - 1; // top-most heightmap blocker

                int y;
                for (y = getDimensionData().getMaxHeight(); y > top; --y) {
                    // all the blocks above & including the top-most block in a column are exposed to sun and
                    // thus have a skylight value of 15
                    this.setBlockSkyLight(x, y, z, 15);
                }

                int light = 15; // light value that will be applied starting with the next block
                int nextDecrease = 0; // decrease that that will be applied starting with the next block

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

                    // START of checks for the next block
                    Block block = this.getBlockState(x, y, z).toBlock();

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
    public ChunkSection getOrCreateSection(int sectionY) {
        int minSectionY = getDimensionData().getMinSectionY();
        int offsetY = sectionY - minSectionY;
        // sectionY outside the dimension's vertical range (below the floor or above the roof): no section exists.
        if (offsetY < 0 || offsetY >= chunk.sections.length) return null;
        if(chunk.sections[offsetY] == null) {
            chunk.sections[offsetY] = new ChunkSection(
                    (byte) (offsetY + minSectionY),
                    chunk.biomeSections[offsetY]
            );
        }
        return chunk.sections[offsetY];
    }

    public ChunkSection getSection(int fY) {
        return this.chunk.sections[fY - getDimensionData().getMinSectionY()];
    }

    public void setSection(int fY, ChunkSection section) {
        int index = fY - getDimensionData().getMinSectionY();
        this.chunk.sections[index] = section;

        if (section != null) {
            this.chunk.biomeSections[index] = section.biomes();
        }

        ChunkRainHeightMap.invalidateAll(chunk);
        setChanged();
    }

    public BlockState getBlockState(int x, int y, int z) {
        ChunkSection section = getSection(y >> 4);
        if (section == null) return BlockAir.STATE;
        return section.getBlockState(x, y & 0x0f, z, 0);
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        ChunkSection section = getSection(y >> 4);
        if (section == null) return BlockAir.STATE;
        return section.getBlockState(x, y & 0x0f, z, layer);
    }

    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        ChunkSection section = getOrCreateSection(y >> 4);
        if (section == null) return BlockAir.STATE;

        int localY = y & 0x0f;
        BlockState oldState = section.getBlockState(x, localY, z, layer);
        long heightMasks = 0L;

        if (oldState != blockstate && chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING) {
            heightMasks = ChunkHeightMap.cellMasksWithStateChange(section, x, localY, z, layer, oldState, blockstate);
        }

        section.setBlockState(x, localY, z, blockstate, layer, oldState);

        if (oldState != blockstate) {
            ChunkRainHeightMap.invalidateAfterBlockChange(chunk, x, y, z);

            int oldHeightMask = (int) (heightMasks >>> 32);
            int newHeightMask = (int) heightMasks;
            if (oldHeightMask != newHeightMask) {
                ChunkHeightMap.updateAfterMaskChange(chunk, x, y, z, oldHeightMask, newHeightMask);
            }

            chunk.updateBorderBlockMap(x, z, oldState, blockstate);
        }

        return oldState;
    }

    public void setBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        ChunkSection section = getOrCreateSection(y >> 4);
        if (section == null) return;

        int localY = y & 0x0f;
        BlockState oldState = section.getBlockState(x, localY, z, layer);
        long heightMasks = 0L;

        if (oldState != blockstate && chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING) {
            heightMasks = ChunkHeightMap.cellMasksWithStateChange(section, x, localY, z, layer, oldState, blockstate);
        }

        section.setBlockState(x, localY, z, blockstate, layer, oldState);

        if (oldState != blockstate) {
            ChunkRainHeightMap.invalidateAfterBlockChange(chunk, x, y, z);

            int oldHeightMask = (int) (heightMasks >>> 32);
            int newHeightMask = (int) heightMasks;
            if (oldHeightMask != newHeightMask) {
                ChunkHeightMap.updateAfterMaskChange(chunk, x, y, z, oldHeightMask, newHeightMask);
            }

            chunk.updateBorderBlockMap(x, z, oldState, blockstate);
        }
    }

    /**
     * Returns the Border Block state for a local X/Z column without acquiring additional chunk locks.
     */
    @ApiStatus.Internal
    public boolean hasBorderBlock(int localX, int localZ) {
        return chunk.hasBorderBlockInternal(localX, localZ);
    }

    public int getBlockSkyLight(int x, int y, int z) {
        ChunkSection section = getSection(y >> 4);
        if (section == null) return 0;
        return section.getBlockSkyLight(x, y & 0x0f, z);
    }

    public void setBlockSkyLight(int x, int y, int z, int level) {
        ChunkSection section = getOrCreateSection(y >> 4);
        if(section != null) {
            section.setBlockSkyLight(x, y & 0x0f, z, (byte) level);
        }
    }

    public int getBlockLight(int x, int y, int z) {
        ChunkSection section = getSection(y >> 4);
        if (section == null) return 0;
        return section.getBlockLight(x, y & 0x0f, z);
    }

    public void setBlockLight(int x, int y, int z, int level) {
        ChunkSection section = getOrCreateSection(y >> 4);
        if (section == null) return; // y outside world height
        section.setBlockLight(x, y & 0x0f, z, (byte) level);
    }

    /**
     * Gets highest block in this (x,z)
     *
     * @param x the x 0~15
     * @param z the z 0~15
     */
    public int getHighestBlockAt(int x, int z) {
        for (int y = getDimensionData().getMaxHeight(); y >= getDimensionData().getMinHeight(); --y) {
            if (getBlockState(x, y, z) != BlockAir.STATE) {
                this.setHeightMap(x, z, y + 1);
                return y;
            }
        }
        return getDimensionData().getMinHeight();
    }

    /**
     * Recalculate height map for this chunk.
     */
    public int recalculateHeightMapColumn(int x, int z) {
        return ChunkHeightMap.recalculateColumn(chunk, x, z);
    }

    public void recalculateHeightMap() {
        ChunkHeightMap.recalculateAll(chunk);
    }

    /**
     * Recalculates the complete render heightmap without acquiring chunk locks.
     */
    public void recalculateRenderHeightMap() {
        ChunkHeightMap.recalculateRender(chunk);
    }

    /**
     * Gets the first free Y above the highest precipitation obstruction without acquiring chunk locks.
     */
    public int getRainHeight(int x, int z) {
        return ChunkRainHeightMap.get(chunk, x, z);
    }

    public int getHeightMap(int x, int z) {
        return this.chunk.heightMap[(z << 4) | x] + getDimensionData().getMinHeight();
    }

    public void setHeightMap(int x, int z, int value) {
        this.chunk.heightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
    }

    /**
     * Returns the render heightmap value without acquiring chunk locks.
     *
     * @param x local X
     * @param z local Z
     * @return render height
     */
    public int getRenderHeightMap(int x, int z) {
        return this.chunk.renderHeightMap[(z << 4) | x] + getDimensionData().getMinHeight();
    }

    /**
     * Sets the render heightmap value without acquiring chunk locks.
     *
     * @param x local X
     * @param z local Z
     * @param value render height
     */
    public void setRenderHeightMap(int x, int z, int value) {
        this.chunk.renderHeightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
    }

    public int getBiomeId(int x, int y, int z) {
        int sectionIndex = (y >> 4) - getDimensionData().getMinSectionY();
        if (sectionIndex < 0 || sectionIndex >= chunk.biomeSections.length) return BiomeID.PLAINS;
        return chunk.biomeSections[sectionIndex].get(IChunk.index(x, y & 0x0f, z));
    }

    public void setBiomeId(int x, int y, int z, int biomeId) {
        int sectionIndex = (y >> 4) - getDimensionData().getMinSectionY();
        if (sectionIndex < 0 || sectionIndex >= chunk.biomeSections.length) return;

        setChanged();
        chunk.biomeSections[sectionIndex].set(IChunk.index(x, y & 0x0f, z), ChunkSection.boxBiomeId(biomeId));
        chunk.biomeState.updateBiome(biomeId);
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

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setLightPopulated(boolean value) {
        chunk.setLightPopulated(value);
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setLightPopulated() {
        chunk.setLightPopulated();
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public ChunkState getChunkState() {
        return ChunkState.fromFinalizationState(chunk.getFinalizationState());
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setChunkState(ChunkState chunkState) {
        chunk.setFinalizationState(chunkState.toFinalizationState());
    }

    /**
     * Returns the transient runtime generation-tree state.
     *
     * @return runtime generation state
     */
    @ApiStatus.Internal
    public ChunkGenerationState getGenerationState() {
        return chunk.getGenerationState();
    }

    /**
     * Returns the chunk finalization state.
     *
     * @return finalization state
     */
    public ChunkFinalizationState getFinalizationState() {
        return chunk.getFinalizationState();
    }

    /**
     * Sets the chunk finalization state.
     *
     * @param finalizationState finalization state
     */
    public void setFinalizationState(ChunkFinalizationState finalizationState) {
        chunk.setFinalizationState(finalizationState);
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

    /**
     * Returns the persisted biome state associated with the chunk.
     *
     * @return biome state
     */
    public BiomeState getBiomeState() {
        return chunk.getBiomeState();
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

    @Deprecated(since = "3.1.0", forRemoval = true)
    public boolean isGenerated() {
        return chunk.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING;
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public boolean isPopulated() {
        return chunk.getFinalizationState() == ChunkFinalizationState.DONE;
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setGenerated() {
        chunk.setFinalizationState(ChunkFinalizationState.NEEDS_POPULATION);
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setPopulated() {
        chunk.setFinalizationState(ChunkFinalizationState.DONE);
    }
}
