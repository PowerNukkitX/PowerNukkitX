package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;

/**
 * @author Kaooot
 */
public class SetLocalPlayerAsInitializedHandler implements PacketHandler<SetLocalPlayerAsInitializedPacket> {

    @Override
    public void handle(SetLocalPlayerAsInitializedPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (player == null) {
            return;
        }
        player.onPlayerLocallyInitialized();
        player.scheduleUpdate();
    }
}
