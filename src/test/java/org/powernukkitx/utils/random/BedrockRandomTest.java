package org.powernukkitx.utils.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Verifies the MT19937 output adapters.
 *
 * @author Curse
 */
public class BedrockRandomTest {

    @Test
    void nextIntMatchesBdsMt19937() {
        BedrockRandom random = new BedrockRandom(5489);

        Assertions.assertArrayEquals(
                new int[]{
                        1749605806,
                        290934651,
                        1945173367,
                        1793167292,
                        272702102
                },
                new int[]{
                        random.nextInt(),
                        random.nextInt(),
                        random.nextInt(),
                        random.nextInt(),
                        random.nextInt()
                }
        );
    }

    @Test
    void boundedIntUsesRawMtOutput() {
        BedrockRandom random = new BedrockRandom(5489);

        Assertions.assertEquals(2, random.nextInt(10));
        Assertions.assertEquals(2, random.nextInt(10));
        Assertions.assertEquals(4, random.nextInt(10));
        Assertions.assertEquals(5, random.nextInt(10));
        Assertions.assertEquals(4, random.nextInt(10));
    }
}
