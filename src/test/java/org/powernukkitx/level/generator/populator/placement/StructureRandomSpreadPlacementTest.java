package org.powernukkitx.level.generator.populator.placement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.utils.random.BedrockRandom;
import org.powernukkitx.utils.random.Xoroshiro128;

/**
 * Tests deterministic random-spread structure candidate placement.
 *
 * @author Curse
 */
public class StructureRandomSpreadPlacementTest {

    private static final long LEVEL_SEED = 123456789L;

    @Test
    void triangularSpreadMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(0x01327220L, 8, 24, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 8, 7));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 26, 53));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -15, 6));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 10, -15));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 2959, -10940));
        Assertions.assertFalse(canGenerate(placement, LEVEL_SEED, 9, 7));
    }

    @Test
    void linearSpreadMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(0x01327220L, 8, 24, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 14, 3));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -16, 11));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 15, -19));
    }

    @Test
    void negativeRegionBoundaryUsesFloorDivision() {
        StructurePlacement placement = placement(0x01327220L, 8, 24, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, 271L, -24, 4));
    }

    @Test
    void pillagerOutpostPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(165745296L, 24, 80, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 38, 15));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -54, 36));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 193, -231));
    }

    @Test
    void randomScatteredLargeFeaturePlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(14357617L, 8, 32, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 23, 17));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 55, 81));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -24, 12));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 19, -11));
    }

    @Test
    void oceanMonumentPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(10387313L, 5, 32, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 15, 9));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 53, 79));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -28, 12));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 24, -24));
    }

    @Test
    void woodlandMansionPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(10387319L, 20, 80, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 32, 34));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 131, 166));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -74, 12));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 25, -42));
    }

    @Test
    void bastionPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(0x01CB0C88L, 4, 30, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 25, 14));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 38, 76));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -16, 22));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 14, -26));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 3691, -13655));
    }

    @Test
    void endCityPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(0x009E7F71L, 11, 20, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 6, 0));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 23, 42));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -16, 3));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 6, -17));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 2461, -9115));
    }

    @Test
    void villagePlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(10387312L, 8, 34, StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 16, 14));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 50, 75));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -20, 11));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 21, -24));
    }

    @Test
    void postSpreadRandomMatchesBdsStructureStartState() {
        StructureRandomSpreadPlacement village = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
                .salt(10387312L)
                .minDistance(8)
                .maxDistance(34)
                .build(), StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);
        BedrockRandom villageRandom = village.createPostSpreadRandom(LEVEL_SEED, 16, 14);

        Assertions.assertEquals(0, villageRandom.nextInt(4));
        Assertions.assertEquals(0.67208534f, villageRandom.nextFloat());

        StructureRandomSpreadPlacement ancientCity = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
                .salt(0x01327220L)
                .minDistance(8)
                .maxDistance(24)
                .build(), StructureRandomSpreadPlacement.SpreadType.TRIANGULAR);
        BedrockRandom ancientCityRandom = ancientCity.createPostSpreadRandom(LEVEL_SEED, 8, 7);

        Assertions.assertEquals(2, ancientCityRandom.nextInt(4));
        Assertions.assertEquals(0.8883725f, ancientCityRandom.nextFloat());

        StructureRandomSpreadPlacement netherComplex = new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
                .salt(0x01CB0C88L)
                .minDistance(4)
                .maxDistance(30)
                .build(), StructureRandomSpreadPlacement.SpreadType.LINEAR);
        BedrockRandom bastionRandom = netherComplex.createPostSpreadRandom(LEVEL_SEED, 25, 14);
        BedrockRandom fortressRandom = netherComplex.createPostSpreadRandom(LEVEL_SEED, 38, 76);

        Assertions.assertEquals(3, bastionRandom.nextInt(6));
        Assertions.assertEquals(3, bastionRandom.nextInt(4));
        Assertions.assertEquals(0, fortressRandom.nextInt(6));
        Assertions.assertEquals(3, fortressRandom.nextInt(4));
    }

    @Test
    void shipwreckPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(165745295L, 4, 24, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 13, 14));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 38, 51));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -5, 4));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 11, -6));
    }

    @Test
    void oceanRuinPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(14357621L, 8, 20, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 9, 1));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 26, 44));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -18, 11));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 9, -16));
    }

    @Test
    void ruinedPortalPlacementMatchesBdsPlacementVectors() {
        StructurePlacement overworld = placement(40552231L, 15, 40, StructureRandomSpreadPlacement.SpreadType.LINEAR);
        StructurePlacement nether = placement(40552231L, 10, 25, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(overworld, LEVEL_SEED, 17, 17));
        Assertions.assertTrue(canGenerate(overworld, LEVEL_SEED, 52, 97));
        Assertions.assertTrue(canGenerate(overworld, LEVEL_SEED, -38, 21));
        Assertions.assertTrue(canGenerate(overworld, LEVEL_SEED, 24, -27));

        Assertions.assertTrue(canGenerate(nether, LEVEL_SEED, 7, 2));
        Assertions.assertTrue(canGenerate(nether, LEVEL_SEED, 32, 62));
        Assertions.assertTrue(canGenerate(nether, LEVEL_SEED, -18, 11));
        Assertions.assertTrue(canGenerate(nether, LEVEL_SEED, 14, -22));
    }

    @Test
    void trialChambersPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(94251327L, 12, 34, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 2, 7));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 52, 82));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -20, 9));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 8, -33));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 4198, -15490));
    }

    @Test
    void trailRuinsPlacementMatchesBdsPlacementVectors() {
        StructurePlacement placement = placement(83469867L, 8, 34, StructureRandomSpreadPlacement.SpreadType.LINEAR);

        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 16, 13));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 50, 79));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, -16, 21));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 1, -33));
        Assertions.assertTrue(canGenerate(placement, LEVEL_SEED, 4195, -15489));
        Assertions.assertFalse(canGenerate(placement, LEVEL_SEED, 17, 13));
    }

    private static StructurePlacement placement(long salt, int separation, int spacing, StructureRandomSpreadPlacement.SpreadType spreadType) {
        return new StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings.builder()
                .salt(salt)
                .minDistance(separation)
                .maxDistance(spacing)
                .build(), spreadType);
    }

    private static boolean canGenerate(StructurePlacement placement, long levelSeed, int chunkX, int chunkZ) {
        return placement.canGenerate(levelSeed, new Xoroshiro128(levelSeed), chunkX, chunkZ, 0);
    }
}
