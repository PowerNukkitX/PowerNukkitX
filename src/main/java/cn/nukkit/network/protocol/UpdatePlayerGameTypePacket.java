package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.GameType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlayerGameTypePacket extends DataPacket {
    public GameType gameType;
    public long entityId;
    public long tick;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.gameType = GameType.from(byteBuf.readVarInt());
        this.entityId = byteBuf.readVarLong();
        this.tick = byteBuf.readUnsignedVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.gameType.ordinal());
        byteBuf.writeVarLong(entityId);
        byteBuf.writeUnsignedVarLong(tick);
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
