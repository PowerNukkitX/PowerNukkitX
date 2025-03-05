package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EntityLink;
import lombok.*;

/**
 * @since 15-10-22
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetEntityLinkPacket extends DataPacket {
    public long vehicleUniqueId; //from
    public long riderUniqueId; //to
    public EntityLink.Type type;
    public byte immediate;
    public boolean riderInitiated = false;
    public float vehicleAngularVelocity;

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
        byteBuf.writeFloatLE(this.vehicleAngularVelocity);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_ENTITY_LINK_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
