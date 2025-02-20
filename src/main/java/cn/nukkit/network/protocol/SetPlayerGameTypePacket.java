package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetPlayerGameTypePacket extends DataPacket {
    public int gamemode;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.gamemode = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.gamemode);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
