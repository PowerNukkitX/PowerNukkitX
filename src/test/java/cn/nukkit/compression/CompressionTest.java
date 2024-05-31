package cn.nukkit.compression;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.ByteBuffer;


@ExtendWith(GameMockExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompressionTest {
    @Test
    @Order(1)
    void testDirectBuffer() throws IllegalAccessException {
        Server.getInstance().getSettings().networkSettings().compressionBufferSize(CompressionProvider.MAX_INFLATE_LEN);
        ThreadLocal<ByteBuffer> directBuffer = (ThreadLocal<ByteBuffer>) FieldUtils.readDeclaredStaticField(LibDeflateThreadLocal.class, "DIRECT_BUFFER", true);
        ByteBuffer byteBuffer = directBuffer.get();
        Assertions.assertEquals(0, byteBuffer.position());
        Assertions.assertEquals(10485760, byteBuffer.capacity());
        Assertions.assertEquals(10485760, byteBuffer.limit());
    }

    @Test
    @Order(2)
    void testLibDeflateInflate() throws IOException {
        Server.getInstance().getSettings().networkSettings().compressionBufferSize(512);
        ZlibChooser.setProvider(3);
        ZlibProvider currentProvider = ZlibChooser.getCurrentProvider();
        byte[] bytes = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            bytes[i] = (byte) i;
        }
        byte[] deflate = currentProvider.deflate(bytes, 7, false);
        byte[] inflate = currentProvider.inflate(deflate, 1024 * 1024, false);
        Assertions.assertArrayEquals(bytes, inflate);
    }

    static byte[] testInput = new byte[]{
            123, 99, 12, -23, -43, -45, -66, 123, 12, 43, 54, 65, 67, 88,
            -123, 123, 31, 23, -122, 43, 33, 54, 66, 88, 99, 22, -1, 32, -34, 32};
    static byte[] testOutput = new byte[]{-85, 78, -26, 121, 121, -11, -14, -66, 106, 30, 109, 51, 71, -25, -120, -42, 106, 121, -15, 54, 109, 69, 51, -89, -120, 100, -79, -1, 10, -9, 20, 0};

    @Test
    void testZlibOriginal() throws Exception {
        ZlibChooser.setProvider(0);
        ZlibProvider currentProvider = ZlibChooser.getCurrentProvider();
        byte[] deflate = currentProvider.deflate(testInput, 7, true);
        Assertions.assertArrayEquals(testOutput, deflate);
        byte[] inflate = currentProvider.inflate(deflate, testInput.length, true);
        Assertions.assertArrayEquals(testInput, inflate);
    }

    @Test
    void testZlibSingleThreadLowMem() throws Exception {
        ZlibChooser.setProvider(1);
        ZlibProvider currentProvider = ZlibChooser.getCurrentProvider();
        byte[] deflate = currentProvider.deflate(testInput, 7, true);
        Assertions.assertArrayEquals(testOutput, deflate);
        byte[] inflate = currentProvider.inflate(deflate, testInput.length, true);
        Assertions.assertArrayEquals(testInput, inflate);
    }

    @Test
    void testZlibThreadLocal() throws Exception {
        ZlibChooser.setProvider(2);
        ZlibProvider currentProvider = ZlibChooser.getCurrentProvider();
        byte[] deflate = currentProvider.deflate(testInput, 7, true);
        Assertions.assertArrayEquals(testOutput, deflate);
        byte[] inflate = currentProvider.inflate(deflate, testInput.length, true);
        Assertions.assertArrayEquals(testInput, inflate);
    }


    @Test
    void testSnappy() throws Exception {
        byte[] deflate = CompressionProvider.SNAPPY.compress(testInput, 7);
        byte[] decompress = CompressionProvider.SNAPPY.decompress(deflate);
        Assertions.assertArrayEquals(testInput, decompress);
    }
}
