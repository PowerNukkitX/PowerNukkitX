package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkit.version.Version;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourcePackDataInfoPacketTest {

    ResourcePackDataInfoPacket packet;

    @BeforeEach
    void setUp() {
        packet = new ResourcePackDataInfoPacket();
    }

    @Test
    void encodeDecode() {
        packet.sha256 = new byte[]{1,2,3};
        packet.encode();
        packet.getUnsignedVarInt();
        packet.decode();
        assertEquals(new ByteArrayList(new byte[]{1, 2, 3}), new ByteArrayList(packet.sha256));
        packet.packId = new UUID(1,2);
        packet.setPackVersion(new Version("3.4.5"));
        packet.encode();
        packet.getUnsignedVarInt();
        packet.decode();
        assertEquals(new UUID(1, 2), packet.packId);
        assertEquals(new Version("3.4.5"), packet.getPackVersion());
    }
}
