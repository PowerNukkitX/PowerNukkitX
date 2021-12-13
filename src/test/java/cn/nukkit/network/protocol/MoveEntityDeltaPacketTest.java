package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-12-13
 */
@PowerNukkitOnly
@Since("FUTURE")
class MoveEntityDeltaPacketTest {

    MoveEntityDeltaPacket packet;

    @BeforeEach
    void setUp() {
        packet = new MoveEntityDeltaPacket();
        packet.x = 1f;
        packet.y = 2f;
        packet.z = 3f;
        packet.pitch = 4f;
        packet.yaw = 5f;
        packet.headYaw = 6f;
    }

    @Test
    void x() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_X;
        packet = encodeDecode();
        assertEquals(1f, packet.x);
        assertEquals(0f, packet.y);
        assertEquals(0f, packet.z);
        assertEquals(0f, packet.pitch);
        assertEquals(0f, packet.yaw);
        assertEquals(0f, packet.headYaw);
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    @Test
    void y() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_Y;
        packet = encodeDecode();
        assertEquals(0f, packet.x);
        assertEquals(2f, packet.y);
        assertEquals(0f, packet.z);
        assertEquals(0f, packet.pitch);
        assertEquals(0f, packet.yaw);
        assertEquals(0f, packet.headYaw);
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    @Test
    void z() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_Z;
        packet = encodeDecode();
        assertEquals(0f, packet.x);
        assertEquals(0f, packet.y);
        assertEquals(3f, packet.z);
        assertEquals(0f, packet.pitch);
        assertEquals(0f, packet.yaw);
        assertEquals(0f, packet.headYaw);
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    @Test
    void pitch() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_PITCH;
        packet = encodeDecode();
        assertEquals(0f, packet.x);
        assertEquals(0f, packet.y);
        assertEquals(0f, packet.z);
        assertEquals(impreciseRotation(4f), packet.pitch);
        assertEquals(0f, packet.yaw);
        assertEquals(0f, packet.headYaw);
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    @Test
    void yaw() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_YAW;
        packet = encodeDecode();
        assertEquals(0f, packet.x);
        assertEquals(0f, packet.y);
        assertEquals(0f, packet.z);
        assertEquals(0f, packet.pitch);
        assertEquals(impreciseRotation(5f), packet.yaw);
        assertEquals(0f, packet.headYaw);
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    @Test
    void headYaw() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW;
        packet = encodeDecode();
        assertEquals(0f, packet.x);
        assertEquals(0f, packet.y);
        assertEquals(0f, packet.z);
        assertEquals(0f, packet.pitch);
        assertEquals(0f, packet.yaw);
        assertEquals(impreciseRotation(6f), packet.headYaw);
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    @Test
    void xyz() {
        packet.flags = MoveEntityDeltaPacket.FLAG_HAS_X | MoveEntityDeltaPacket.FLAG_HAS_Y | MoveEntityDeltaPacket.FLAG_HAS_Z;
        packet = encodeDecode();
        assertEquals(1f, packet.x);
        assertEquals(2f, packet.y);
        assertEquals(3f, packet.z);
        assertEquals(0f, packet.pitch);
        assertEquals(0f, packet.yaw);
        assertEquals(0f, packet.headYaw);
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_X));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Y));
        assertTrue(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_Z));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_PITCH));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_YAW));
        assertFalse(packet.hasFlag(MoveEntityDeltaPacket.FLAG_HAS_HEAD_YAW));
    }

    private MoveEntityDeltaPacket encodeDecode() {
        packet.encode();
        MoveEntityDeltaPacket other = new MoveEntityDeltaPacket();
        other.setBuffer(packet.getBuffer());
        other.getUnsignedVarInt();
        other.decode();
        return other;
    }

    private float impreciseRotation(float rotation) {
        return ((byte) (rotation / (360F / 256F))) * (360F / 256F);
    }
}
