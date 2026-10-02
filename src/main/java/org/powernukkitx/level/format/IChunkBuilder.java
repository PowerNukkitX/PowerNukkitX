package org.powernukkitx.level.format;

import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.structure.AabbVolumes;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.List;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public interface IChunkBuilder {
    IChunkBuilder chunkX(int chunkX);

    int getChunkX();

    IChunkBuilder chunkZ(int chunkZ);

    int getChunkZ();

    /**
     * Sets the chunk finalization state.
     *
     * @param finalizationState finalization state
     * @return this builder
     */
    IChunkBuilder finalizationState(
            ChunkFinalizationState finalizationState
    );

    @Deprecated(since = "3.1.0", forRemoval = true)
    default IChunkBuilder state(ChunkState state) {
        return this.finalizationState(state.toFinalizationState());
    }

    IChunkBuilder levelProvider(LevelProvider levelProvider);

    LevelProvider getLevelProvider();

    DimensionData getDimensionData();

    IChunkBuilder sections(ChunkSection[] sections);

    ChunkSection[] getSections();

    /**
     * Sets the per-section biome palettes.
     *
     * @param biomeSections biome section palettes
     * @return this builder
     */
    IChunkBuilder biomeSections(Palette<Integer>[] biomeSections);

    /**
     * Returns the configured per-section biome palettes.
     *
     * @return biome section palettes
     */
    Palette<Integer>[] getBiomeSections();

    IChunkBuilder heightMap(short[] heightMap);

    IChunkBuilder entities(List<CompoundTag> entities);

    IChunkBuilder blockEntities(List<CompoundTag> blockEntities);

    IChunkBuilder extraData(CompoundTag extraData);

    /**
     * Sets LevelChunkMetaData reference state loaded for the chunk.
     */
    IChunkBuilder levelChunkMetaData(LevelChunkMetaData levelChunkMetaData);

    /**
     * Sets AABBVolumes data loaded for the chunk.
     */
    IChunkBuilder aabbVolumes(AabbVolumes aabbVolumes);

    /**
     * Sets the Bedrock biome state loaded for the chunk.
     */
    IChunkBuilder biomeState(BiomeState biomeState);

    /**
     * Sets the 16x16 Border Block column map loaded for the chunk.
     */
    IChunkBuilder borderBlockMap(boolean[] borderBlockMap);

    IChunk build();

    IChunk emptyChunk(int chunkX, int chunkZ);
}
