package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RemoveVolumeEntityPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.REMOVE_VOLUME_ENTITY_PACKET;

    public long id;
    /**
     * @since v503
     */
    public int dimension;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        id = byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt((int) id);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
