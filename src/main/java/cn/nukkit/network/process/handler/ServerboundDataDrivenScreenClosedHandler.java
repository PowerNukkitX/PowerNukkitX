package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.ddui.DataDrivenScreen;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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
