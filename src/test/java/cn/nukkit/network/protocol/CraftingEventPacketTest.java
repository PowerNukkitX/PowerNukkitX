package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.var;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-09-25
 */
class CraftingEventPacketTest {

    @Test
    void encodeDecode() {
        var packet = new CraftingEventPacket();
        packet.type = 1;
        packet.id = UUID.randomUUID();
        packet.input = new Item[]{new Item(1000), new Item(1001)};
        packet.output = new Item[]{new Item(1002), new Item(1003)};

        packet.encode();
        var packet2 = new CraftingEventPacket();
        packet2.setBuffer(packet.getBuffer());
        packet2.decode();

        assertEquals(packet, packet2);
    }
}
