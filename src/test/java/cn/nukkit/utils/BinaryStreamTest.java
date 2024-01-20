package cn.nukkit.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BinaryStreamTest {
    @Test
    void testPutUnsignedVarInt() {
        BinaryStream binaryStream = new BinaryStream();
        binaryStream.putUnsignedVarInt(Integer.toUnsignedLong(-1848593788));

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        ByteBufVarInt.writeUnsignedInt(byteBuf, (int) Integer.toUnsignedLong(-1848593788));
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        BinaryStream binaryStream2 = new BinaryStream();
        binaryStream2.setBuffer(bytes);

        Assertions.assertArrayEquals(binaryStream.getBuffer(), bytes);
        Assertions.assertEquals(binaryStream.getUnsignedVarInt(), binaryStream2.getUnsignedVarInt());
    }
}
