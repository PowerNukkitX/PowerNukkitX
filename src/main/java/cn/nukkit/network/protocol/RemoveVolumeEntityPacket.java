package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RemoveVolumeEntityPacket extends DataPacket {
    public long id;
    /**
     * @since v503
     */
    public int dimension;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        id = byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt((int) id);
    }

    @Override
    public int pid() {
        return ProtocolInfo.REMOVE_VOLUME_ENTITY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
