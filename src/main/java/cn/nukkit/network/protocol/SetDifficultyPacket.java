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
public class SetDifficultyPacket extends DataPacket {
    public int difficulty;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.difficulty = (int) byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.difficulty);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_DIFFICULTY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
