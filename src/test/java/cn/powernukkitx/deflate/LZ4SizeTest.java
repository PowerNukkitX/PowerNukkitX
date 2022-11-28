package cn.powernukkitx.deflate;

import net.jpountz.lz4.LZ4Factory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

public class LZ4SizeTest {
    LZ4Factory factory = LZ4Factory.fastestInstance();
    @Test
    public void test() {
        byte[] data = new byte[4096];
        for (int i=1024;i<4096;i++) {
            data[i] = (byte) ThreadLocalRandom.current().nextInt(1);
        }
        var a = factory.fastCompressor().compress(data);
        var b = factory.highCompressor(17).compress(data);
        var c = factory.highCompressor().compress(a);
        var d = factory.fastDecompressor().decompress(a, 4096);
        System.out.println(a.length);
        System.out.println(b.length);
        System.out.println(c.length);
        System.out.println(d.length);
    }
}
