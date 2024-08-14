package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

import java.util.stream.Stream;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MobArmorEquipmentPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;

    public long eid;
    public Item[] slots = new Item[4];
    public Item body = Item.AIR;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId();
        this.slots = new Item[4];
        this.slots[0] = byteBuf.readSlot();
        this.slots[1] = byteBuf.readSlot();
        this.slots[2] = byteBuf.readSlot();
        this.slots[3] = byteBuf.readSlot();
        this.body = byteBuf.readSlot();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeSlot(this.slots[0]);
        byteBuf.writeSlot(this.slots[1]);
        byteBuf.writeSlot(this.slots[2]);
        byteBuf.writeSlot(this.slots[3]);
        byteBuf.writeSlot(this.body);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
