package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourcePackChunkDataPacketTest {

    ResourcePackChunkDataPacket packet;

    @BeforeEach
    void setUp() {
        packet = new ResourcePackChunkDataPacket();
    }

    @Test
    void encodeDecode() {
        packet.data = new byte[]{1,2,3};
        packet.encode();
        packet.getUnsignedVarInt();
        packet.decode();
        assertEquals(new ByteArrayList(new byte[]{1,2,3}), new ByteArrayList(packet.data));
    }
}
