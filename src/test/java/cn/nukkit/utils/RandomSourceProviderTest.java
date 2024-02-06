package cn.nukkit.utils;

import cn.nukkit.utils.random.RandomSourceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RandomSourceProviderTest {
    static RandomSourceProvider randomSource;

    @BeforeAll
    static void test_create() {
        randomSource = RandomSourceProvider.create();
    }

    @Test
    void nextInt() {
        int i = randomSource.nextInt();
        Assertions.assertTrue(Integer.MAX_VALUE >= i && i >= Integer.MIN_VALUE);
    }

    @Test
    void nextIntBound() {
        int i = randomSource.nextInt(5);
        Assertions.assertTrue(5 >= i && i >= 0);
    }

    @Test
    void nextIntMinMax() {
        int i = randomSource.nextInt(4, 10);
        Assertions.assertTrue(10 >= i && i >= 4);
    }

    @Test
    void nextGaussian() {
        for (int i = 0; i < 100; i++) {
            double v = randomSource.nextGaussian();
            Assertions.assertTrue(v <= 1 && v >= -1, String.valueOf(v));
        }
    }
}
