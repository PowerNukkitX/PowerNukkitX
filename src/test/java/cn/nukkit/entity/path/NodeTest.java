package cn.nukkit.entity.path;

import org.junit.jupiter.api.Test;

public class NodeTest {
    static int mySqrt(int x) {
        int m = 0x40000000, y = 0, b;
        while (m != 0) {
            b = y | m;
            y = y >> 1;
            if (x >= b) {
                x = x - b;
                y = y | m;
            }
            m = m >> 2;
        }
        return y;
    }

    @Test
    public void testSqrtTime() {
        long start = System.currentTimeMillis();
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += mySqrt(i);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(sum);

        sum = 0;
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            sum += (int) Math.sqrt(i);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(sum);
    }
}
