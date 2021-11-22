package cn.nukkit.network.protocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkit.version.Version;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResourcePackChunkRequestPacketTest {

    ResourcePackChunkRequestPacket packet;

    @BeforeEach
    void setUp() {
        packet = new ResourcePackChunkRequestPacket();
    }

    @Test
    void encodeDecode() {
        packet.setPackId(new UUID(1,2));
        packet.setPackVersion(new Version("3.4.5"));
        packet.chunkIndex = 6;
        packet.encode();
        packet.getUnsignedVarInt();
        packet.decode();
        assertEquals(new UUID(1,2), packet.getPackId());
        assertEquals(new Version("3.4.5"), packet.getPackVersion());
        assertEquals(6, packet.chunkIndex);
    }
}
