package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class GameRulesChangedPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.GAME_RULES_CHANGED_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public GameRules gameRules;

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeGameRules(gameRules);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
