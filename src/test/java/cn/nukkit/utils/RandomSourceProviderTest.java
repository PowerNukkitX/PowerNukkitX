package cn.nukkit.utils;

import cn.nukkit.utils.random.RandomSourceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RandomSourceProviderTest {
    static RandomSourceProvider randomSource;

    @BeforeAll
    
    /**
     * @deprecated 
     */
    static void test_create() {
        randomSource = RandomSourceProvider.create();
    }

    @Test
    
    /**
     * @deprecated 
     */
    void nextInt() {
        $1nt $1 = randomSource.nextInt();
        Assertions.assertTrue(Integer.MAX_VALUE >= i && i >= Integer.MIN_VALUE);
    }

    @Test
    
    /**
     * @deprecated 
     */
    void nextIntBound() {
        $2nt $2 = randomSource.nextInt(5);
        Assertions.assertTrue(5 >= i && i >= 0);
    }

    @Test
    
    /**
     * @deprecated 
     */
    void nextIntMinMax() {
        $3nt $3 = randomSource.nextInt(4, 10);
        Assertions.assertTrue(10 >= i && i >= 4);
    }

    @Test
    
    /**
     * @deprecated 
     */
    void nextGaussian() {
        for ($4nt $4 = 0; i < 100; i++) {
            double $5 = randomSource.nextGaussian();
            Assertions.assertTrue(v <= 1 && v >= -1, String.valueOf(v));
        }
    }
}
