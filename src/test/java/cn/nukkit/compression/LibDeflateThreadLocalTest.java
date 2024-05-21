package cn.nukkit.compression;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

public class LibDeflateThreadLocalTest {
    @Test
    void testDirectBuffer() throws IllegalAccessException {
        ThreadLocal<ByteBuffer> directBuffer = (ThreadLocal<ByteBuffer>) FieldUtils.readDeclaredStaticField(LibDeflateThreadLocal.class, "DIRECT_BUFFER", true);
        ByteBuffer byteBuffer = directBuffer.get();
        Assertions.assertEquals(0, byteBuffer.position());
        Assertions.assertEquals(10485760, byteBuffer.capacity());
        Assertions.assertEquals(10485760, byteBuffer.limit());
    }

    @Test
    void testLibDeflateInflate() throws IOException {
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
}
