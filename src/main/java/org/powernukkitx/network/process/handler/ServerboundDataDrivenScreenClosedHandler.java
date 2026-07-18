package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.ddui.DataDrivenScreen;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDataDrivenScreenClosedPacket;

/**
 * @author Kaooot
 */
public class ServerboundDataDrivenScreenClosedHandler implements PacketHandler<ServerboundDataDrivenScreenClosedPacket> {

    @Override
    public void handle(ServerboundDataDrivenScreenClosedPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        DataDrivenScreen screen = DataDrivenScreen.getScreenByFormId(player, packet.getFormId());
        if (screen != null) {
            screen.removeViewer(player);
        }
    }
}
