package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.ServerboundLoadingScreenPacketType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerboundLoadingScreenPacket extends DataPacket {
    private ServerboundLoadingScreenPacketType type;
    /**
     * Optional int, not present if null
     */
    private Integer loadingScreenId = null;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.type = ServerboundLoadingScreenPacketType.values()[byteBuf.readVarInt()];
        if (byteBuf.readBoolean()) {
            this.loadingScreenId = byteBuf.readIntLE();
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVERBOUND_LOADING_SCREEN_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

}
