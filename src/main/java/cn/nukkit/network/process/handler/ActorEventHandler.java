package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

/**
 * @author Kaooot
 */
public class ActorEventHandler implements PacketHandler<ActorEventPacket> {

    @Override
    public void handle(ActorEventPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        final Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (packet.getType().equals(ActorEvent.FEED)) {
            if (packet.getData() == 0 || packet.getTargetRuntimeID() != player.getId()) {
                return;
            }

            Item hand = player.getInventory().getItemInMainHand();
            if (!(hand instanceof ItemFood)) {
                return;
            }

            final int predictedData = (hand.getRuntimeId() << 16) | hand.getDamage();
            if (packet.getData() != predictedData) {
                return;
            }

            packet.setTargetRuntimeID(player.getId());
            packet.setData(predictedData);

            player.dataPacket(packet);
            Server.broadcastPacket(player.getViewers().values(), packet);
        }
    }
}