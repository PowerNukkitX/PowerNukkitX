package org.powernukkitx.level.generator.populator.placement;

import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

/**
 * Ocean Monument placement with inner and outer biome-area validation.
 *
 * @author Curse
 */
public final class OceanMonumentPlacement extends StructureRandomSpreadPlacement {

    private static final int DEEP_OCEAN_RADIUS = 16;
    private static final int OCEAN_RADIUS = 29;

    /**
     * Creates the Ocean Monument random-spread placement.
     */
    public OceanMonumentPlacement(StructurePlacement.PlacementSettings settings) {
        super(settings, SpreadType.TRIANGULAR);
    }

    @Override
    protected boolean isValidBiome(BiomePicker<?> biomePicker, int chunkX, int chunkZ) {
        int centerX = (chunkX << 4) + 8;
        int centerZ = (chunkZ << 4) + 8;
        return areBiomesValid(biomePicker, centerX, centerZ, DEEP_OCEAN_RADIUS, true)
                && areBiomesValid(biomePicker, centerX, centerZ, OCEAN_RADIUS, false);
    }

    private static boolean areBiomesValid(BiomePicker<?> biomePicker, int centerX, int centerZ, int radius, boolean deepOnly) {
        int minQuartX = (centerX - radius) >> 2;
        int maxQuartX = (centerX + radius) >> 2;
        int minQuartZ = (centerZ - radius) >> 2;
        int maxQuartZ = (centerZ + radius) >> 2;

        for (int quartX = minQuartX; quartX <= maxQuartX; quartX++) {
            int x = quartX << 2;
            for (int quartZ = minQuartZ; quartZ <= maxQuartZ; quartZ++) {
                int biome = sampleBiome(biomePicker, x, quartZ << 2);
                if (deepOnly ? !isDeepOceanBiome(biome) : !isOceanOrRiverBiome(biome)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int sampleBiome(BiomePicker<?> biomePicker, int x, int z) {
        if (biomePicker instanceof OverworldBiomePicker overworldBiomePicker) {
            return overworldBiomePicker.pickRaw(x, SEA_LEVEL, z).getBiomeId();
        }
        return biomePicker.pick(x, SEA_LEVEL, z).getBiomeId();
    }

    private static boolean isDeepOceanBiome(int biome) {
        return switch (biome) {
            case BiomeID.DEEP_OCEAN,
                 BiomeID.DEEP_WARM_OCEAN,
                 BiomeID.DEEP_LUKEWARM_OCEAN,
                 BiomeID.DEEP_COLD_OCEAN,
                 BiomeID.DEEP_FROZEN_OCEAN -> true;
            default -> false;
        };
    }

    private static boolean isOceanOrRiverBiome(int biome) {
        return switch (biome) {
            case BiomeID.OCEAN,
                 BiomeID.RIVER,
                 BiomeID.FROZEN_RIVER,
                 BiomeID.DEEP_OCEAN,
                 BiomeID.WARM_OCEAN,
                 BiomeID.DEEP_WARM_OCEAN,
                 BiomeID.LUKEWARM_OCEAN,
                 BiomeID.DEEP_LUKEWARM_OCEAN,
                 BiomeID.COLD_OCEAN,
                 BiomeID.DEEP_COLD_OCEAN,
                 BiomeID.FROZEN_OCEAN,
                 BiomeID.DEEP_FROZEN_OCEAN -> true;
            default -> false;
        };
    }
}
