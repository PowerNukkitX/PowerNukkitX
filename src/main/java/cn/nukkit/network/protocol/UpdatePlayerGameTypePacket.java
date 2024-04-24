package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.GameType;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlayerGameTypePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET;
    public GameType gameType;
    public long entityId;
    public int tick;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.gameType = GameType.from(byteBuf.readVarInt());
        this.entityId = byteBuf.readVarLong();
        this.tick = byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.gameType.ordinal());
        byteBuf.writeVarLong(entityId);
        byteBuf.writeUnsignedVarInt(tick);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
