package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "namedtag")
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEquipmentPacket extends DataPacket {
    public int windowId;
    public int windowType;
    public int size = 0;
    public long eid;
    public byte[] namedtag;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeByte((byte) this.windowType);
        byteBuf.writeVarInt(size);
        byteBuf.writeEntityUniqueId(this.eid);
        byteBuf.writeBytes(this.namedtag);
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
