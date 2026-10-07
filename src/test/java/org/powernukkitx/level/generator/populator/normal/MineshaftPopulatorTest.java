package org.powernukkitx.level.generator.populator.normal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests abandoned mineshaft start placement.
 *
 * @author Curse
 */
public class MineshaftPopulatorTest {

    private static final long LEVEL_SEED = 123456789L;

    @Test
    void placementMatchesBdsVectors() {
        Assertions.assertTrue(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, 10, -11));
        Assertions.assertTrue(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, -4, -13));
        Assertions.assertTrue(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, -15, 4));
        Assertions.assertTrue(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, -25, 14));
        Assertions.assertTrue(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, 40, 18));
    }

    @Test
    void distanceGateMatchesBdsVectors() {
        Assertions.assertFalse(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, 0, 0));
        Assertions.assertFalse(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, 11, -9));
        Assertions.assertFalse(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, -2, -13));
        Assertions.assertFalse(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, 14, 14));
        Assertions.assertFalse(MineshaftPopulator.isMineshaftStart(LEVEL_SEED, -1, 30));
    }
}
