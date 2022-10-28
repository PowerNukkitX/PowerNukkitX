package cn.nukkit.test;

import cn.nukkit.utils.Zlib;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@DisplayName("Zlib")
class ZlibTest {

    @DisplayName("Inflate and Deflate")
    @Test
    void testAll() throws Exception {
        Zlib.setProvider(3);
        for (int i = 0; i < 10000; i++) {
            byte[] in = getRandomBytes(ThreadLocalRandom.current().nextInt(114514));
            byte[] compressed = Zlib.deflate(in);
            byte[] out = Zlib.inflate(compressed);
            assertArrayEquals(in, out);
        }
    }

    public static byte[] getRandomBytes(int length) {
        var data = new byte[length];
        for (int j = 0; j < length; j++) {
            data[j] = (byte) ((Math.random() - 0.5) * 128);
        }
        return data;
    }

}
