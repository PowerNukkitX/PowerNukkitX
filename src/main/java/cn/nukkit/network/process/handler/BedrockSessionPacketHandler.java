package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.PacketHandler;

public class BedrockSessionPacketHandler implements PacketHandler {
    protected final Player player;
    protected final BedrockSession session;
    protected final PlayerHandle handle;

    public BedrockSessionPacketHandler(BedrockSession session) {
        this.player = session.getPlayer();
        this.session = session;
        this.handle = session.getHandle();
    }

    public void handle(DisconnectPacket pk) {
        if (player != null) {
            player.close(pk.message);
        }
    }
}
