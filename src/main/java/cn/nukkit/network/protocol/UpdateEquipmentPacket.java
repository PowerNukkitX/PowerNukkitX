package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString(exclude = "namedtag")
public class UpdateEquipmentPacket extends DataPacket {

    public int windowId;
    public int windowType;
    public int unknown; //TODO: find out what this is (vanilla always sends 0)
    public long eid;
    public byte[] namedtag;


    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeByte((byte) this.windowType);
        byteBuf.writeVarInt(0);//size
        byteBuf.writeEntityUniqueId(this.eid);
        byteBuf.writeBytes(this.namedtag);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
