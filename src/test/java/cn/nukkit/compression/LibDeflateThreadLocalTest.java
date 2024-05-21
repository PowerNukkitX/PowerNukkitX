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
public class LibDeflateThreadLocalTest {
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
}
