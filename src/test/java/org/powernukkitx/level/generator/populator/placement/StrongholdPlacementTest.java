package org.powernukkitx.level.generator.populator.placement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.result.BiomeResult;
import org.powernukkitx.utils.random.NukkitRandom;
import org.powernukkitx.utils.random.Xoroshiro128;

/**
 * Tests Stronghold placement.
 *
 * @author Curse
 */
public class StrongholdPlacementTest {

    private static final long LEVEL_SEED = 123456789L;

    @Test
    void farFieldPlacementMatchesBdsVectors() {
        StrongholdPlacement placement = placement();

        Assertions.assertTrue(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 81, -145, 0));
        Assertions.assertTrue(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 520, 139, 0));
        Assertions.assertTrue(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 736, -307, 0));
        Assertions.assertFalse(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 110, 75, 0));
        Assertions.assertFalse(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 0, 0, 0));
    }

    @Test
    void initialVillageAnchoredPlacementMatchesBdsAlgorithm() {
        StrongholdPlacement placement = placement();
        BiomePicker<BiomeResult> biomePicker = new PlainsBiomePicker();

        Assertions.assertTrue(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 12, -52, biomePicker));
        Assertions.assertTrue(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), -84, 19, biomePicker));
        Assertions.assertTrue(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), 11, -92, biomePicker));
        Assertions.assertFalse(placement.canGenerate(LEVEL_SEED, new Xoroshiro128(LEVEL_SEED), -51, -11, biomePicker));
    }

    private static StrongholdPlacement placement() {
        StructureRandomSpreadPlacement village = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
                .salt(10387312L)
                .minDistance(8)
                .maxDistance(34)
                .isBiomeValid(biome -> biome == BiomeID.PLAINS)
                .build(), StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);
        return new StrongholdPlacement(village);
    }

    private static final class PlainsBiomePicker extends BiomePicker<BiomeResult> {
        private static final BiomeResult PLAINS = new BiomeResult(BiomeID.PLAINS) {
        };

        private PlainsBiomePicker() {
            super(new NukkitRandom(0));
        }

        @Override
        public BiomeResult pick(int x, int y, int z) {
            return PLAINS;
        }
    }
}
