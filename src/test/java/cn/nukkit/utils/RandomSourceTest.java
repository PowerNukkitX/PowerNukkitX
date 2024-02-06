package cn.nukkit.utils;

import cn.nukkit.utils.random.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RandomSourceTest {
    static RandomSource randomSource;

    @BeforeAll
    static void test_create() {
        randomSource = RandomSource.create();
    }

    @Test
    void nextInt() {
        int i = randomSource.nextInt();
        Assertions.assertTrue(Integer.MAX_VALUE >= i && i >= Integer.MIN_VALUE);
    }

    @Test
    void nextIntBound() {
        int i = randomSource.nextInt(5);
        Assertions.assertTrue(4 >= i && i >= 0);
    }

    @Test
    void nextIntMinMax() {
        int i = randomSource.nextInt(4, 10);
        Assertions.assertTrue(10 >= i && i >= 4);
    }
    @Test
    void d() {
        System.out.println(randomSource.nextGaussian());
    }
}
