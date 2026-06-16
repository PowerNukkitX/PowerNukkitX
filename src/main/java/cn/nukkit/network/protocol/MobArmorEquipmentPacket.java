package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

import java.util.stream.Stream;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MobArmorEquipmentPacket extends DataPacket {
    public long eid;
    public Item[] slots = new Item[4];
    public Item body = Item.AIR;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId();
        this.slots = new Item[4];
        this.slots[0] = byteBuf.readCerealSlot();
        this.slots[1] = byteBuf.readCerealSlot();
        this.slots[2] = byteBuf.readCerealSlot();
        this.slots[3] = byteBuf.readCerealSlot();
        this.body = byteBuf.readCerealSlot();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeCerealSlot(this.slots[0]);
        byteBuf.writeCerealSlot(this.slots[1]);
        byteBuf.writeCerealSlot(this.slots[2]);
        byteBuf.writeCerealSlot(this.slots[3]);
        byteBuf.writeCerealSlot(this.body);
    }

    @Override
    public int pid() {
        return ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
