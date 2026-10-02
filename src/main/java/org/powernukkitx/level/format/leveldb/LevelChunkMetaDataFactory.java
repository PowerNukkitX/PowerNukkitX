package org.powernukkitx.level.format.leveldb;

import org.cloudburstmc.nbt.NbtMap;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelChunkMetaData;
import org.powernukkitx.network.NetworkConstants;

import java.util.Map;
import java.util.TreeMap;

/**
 * Creates and updates LevelChunkMetaData while preserving generation provenance.
 *
 * @author Curse
 */
final class LevelChunkMetaDataFactory {
    private static final String MODERN_BIOME_BASE_GAME_VERSION = "1.18.0";

    private LevelChunkMetaDataFactory() {
    }

    static NbtMap createForChunk(IChunk chunk, int generatorType) {
        return createForChunk(
                chunk,
                generatorType,
                LevelChunkMetaData.CURRENT_NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION
        );
    }

    static NbtMap createForChunk(IChunk chunk, int generatorType, int neighborAwareBlockUpgradeVersion) {
        DimensionData dimensionData = chunk.getProvider().getDimensionData();
        String gameVersion = NetworkConstants.CODEC.getMinecraftVersion();
        NbtMap heightRange = createHeightRange(dimensionData);

        var builder = NbtMap.builder()
                .putString("BiomeBaseGameVersion", MODERN_BIOME_BASE_GAME_VERSION)
                .putString("DimensionName", getDimensionName(dimensionData))
                .putLong("GenerationSeed", chunk.getProvider().getSeed())
                .putInt("GeneratorType", generatorType)
                .putInt(LevelChunkMetaData.NEIGHBOR_AWARE_BLOCK_UPGRADE_VERSION, neighborAwareBlockUpgradeVersion)
                .putString("OriginalBaseGameVersion", gameVersion)
                .putCompound("OriginalDimensionHeightRange", heightRange);

        if (dimensionData.getDimensionId() == Level.DIMENSION_OVERWORLD) {
            builder.putShort("Overworld1_18HeightExtended", (short) 1);
        }

        builder.putShort("SkullFlatteningPerformed", (short) 1);

        if (dimensionData.getDimensionId() == Level.DIMENSION_OVERWORLD) {
            builder.putShort("UnderwaterLavaLakeFixed", (short) 1)
                    .putShort("WorldGenBelowZeroFixed", (short) 1);
        }

        return builder.build();
    }

    static NbtMap updateForSave(NbtMap metadata, IChunk chunk) {
        String gameVersion = NetworkConstants.CODEC.getMinecraftVersion();
        NbtMap heightRange = createHeightRange(chunk.getProvider().getDimensionData());

        if (gameVersion.equals(metadata.get("LastSavedBaseGameVersion")) && heightRange.equals(metadata.get("LastSavedDimensionHeightRange"))) {
            return metadata;
        }

        Map<String, Object> values = new TreeMap<>(metadata);
        values.put("LastSavedBaseGameVersion", gameVersion);
        values.put("LastSavedDimensionHeightRange", heightRange);
        return NbtMap.fromMap(values);
    }

    private static String getDimensionName(DimensionData dimensionData) {
        return switch (dimensionData.getDimensionId()) {
            case Level.DIMENSION_OVERWORLD -> "Overworld";
            case Level.DIMENSION_NETHER -> "Nether";
            case Level.DIMENSION_THE_END -> "TheEnd";
            default -> dimensionData.getDimensionName();
        };
    }

    private static NbtMap createHeightRange(DimensionData dimensionData) {
        return NbtMap.builder()
                .putShort("max", (short) (dimensionData.getMaxHeight() + 1))
                .putShort("min", (short) dimensionData.getMinHeight())
                .build();
    }
}
