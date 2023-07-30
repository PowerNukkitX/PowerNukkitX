package cn.nukkit.event.server;

import cn.nukkit.event.Cancellable;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class DataPacketSendEvent extends ServerEvent implements Cancellable {

    private final DataPacket packet;
    private final Player player;

    public DataPacketSendEvent(Player player, DataPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public DataPacket getPacket() {
        return packet;
    }
}
