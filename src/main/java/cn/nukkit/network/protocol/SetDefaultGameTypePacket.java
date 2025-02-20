package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetDefaultGameTypePacket extends DataPacket {
    public int gamemode;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.gamemode = byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.gamemode);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
