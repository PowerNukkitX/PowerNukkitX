package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameRulesChangedPacket extends DataPacket {
    public GameRules gameRules;

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeGameRules(gameRules);
    }

    @Override
    public int pid() {
        return ProtocolInfo.GAME_RULES_CHANGED_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
