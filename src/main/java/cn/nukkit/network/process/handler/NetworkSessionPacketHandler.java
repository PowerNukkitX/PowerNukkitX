package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.process.PacketHandler;

public class NetworkSessionPacketHandler implements PacketHandler {
    protected final Player player;
    protected final NetworkSession session;
    protected final PlayerHandle handle;

    public NetworkSessionPacketHandler(NetworkSession session) {
        this.player = session.getPlayer();
        this.session = session;
        this.handle = session.getHandle();
    }
}
