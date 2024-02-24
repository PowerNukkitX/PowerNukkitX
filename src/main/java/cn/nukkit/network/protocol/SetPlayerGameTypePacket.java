package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class SetPlayerGameTypePacket extends DataPacket {
    public final static byte NETWORK_ID = ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int gamemode;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.gamemode = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.gamemode);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
