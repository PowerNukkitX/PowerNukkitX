package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashTest {
    @Test
    void hashBlockRoundTrip() {
        int[] xs = {0, 1, -1, 100, -100, 0x1FFFFFF, -0x2000000};
        int[] zs = {0, 5, -5, 200, -200, 0x1FFFFFF, -0x2000000};
        int[] ys = {0, 1, -1, 64, 320, -64};
        for (int x : xs) {
            for (int z : zs) {
                for (int y : ys) {
                    long h = Hash.hashBlock(x, y, z);
                    Assertions.assertEquals(x, Hash.hashBlockX(h), "x mismatch");
                    Assertions.assertEquals(y, Hash.hashBlockY(h), "y mismatch");
                    Assertions.assertEquals(z, Hash.hashBlockZ(h), "z mismatch");
                }
            }
        }
    }

    @Test
    void distinctPositionsDistinctHashes() {
        long a = Hash.hashBlock(1, 2, 3);
        long b = Hash.hashBlock(3, 2, 1);
        Assertions.assertNotEquals(a, b);
    }
}
