package org.powernukkitx.utils.random;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NukkitRandomTest {

    private static final long[] SEEDS = {
            0L,
            1L,
            -1L,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            0x61c8864680b583ebL,
            1234567890123456789L,
            -987654321234567890L
    };

    @Test
    void reseedMatchesCommonsMersenneTwister() {
        NukkitRandom actual = new NukkitRandom(0L);

        for (long seed : SEEDS) {
            actual.setSeed(seed);
            UniformRandomProvider expected = RandomSource.MT.create(seed);
            assertSequence(expected, actual);
        }
    }

    private static void assertSequence(UniformRandomProvider expected, NukkitRandom actual) {
        for (int i = 0; i < 256; i++) {
            assertEquals(expected.nextInt(), actual.nextInt());
            assertEquals(expected.nextLong(), actual.nextLong());
            assertEquals(expected.nextFloat(), actual.nextFloat());
            assertEquals(expected.nextDouble(), actual.nextDouble());
            assertEquals(expected.nextBoolean(), actual.nextBoolean());
            assertEquals(expected.nextInt(1000), actual.nextExclusiveInt(1000));
            assertEquals(expected.nextInt(1001), actual.nextInt(1000));
            assertEquals(expected.nextInt(-1000, 1001), actual.nextInt(-1000, 1000));
            assertEquals(expected.nextInt(501), actual.nextRange(0, 500));
        }
    }
}
