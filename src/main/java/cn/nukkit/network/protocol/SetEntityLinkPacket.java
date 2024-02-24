package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EntityLink;
import lombok.ToString;

/**
 * @since 15-10-22
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public long vehicleUniqueId; //from
    public long riderUniqueId; //to
    public EntityLink.Type type;
    public byte immediate;
    public boolean riderInitiated = false;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityUniqueId(this.vehicleUniqueId);
        byteBuf.writeEntityUniqueId(this.riderUniqueId);
        byteBuf.writeByte((byte) this.type.ordinal());
        byteBuf.writeByte(this.immediate);
        byteBuf.writeBoolean(this.riderInitiated);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
