package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

import java.util.Objects;

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
        final ActorEvent type = packet.getType();
        if (type == null){
            return;
        }

        if (type.equals(ActorEvent.FEED) || type.equals(ActorEvent.DRINK_MILK)) {
            if (packet.getTargetRuntimeID() != player.getId()) {
                return;
            }

            Item hand = player.getInventory().getItemInMainHand();
            if (!hand.isConsumable()) {
                return;
            }

            if (type.equals(ActorEvent.DRINK_MILK) && !(Objects.equals(hand.getId(), ItemID.MILK_BUCKET))){
                return;
            }

            if (packet.getType() == ActorEvent.FEED && packet.getData() == 0) {
                return;
            }

            int predictedData = (hand.getRuntimeId() << 16) | hand.getDamage();
            if (packet.getType() == ActorEvent.FEED && packet.getData() != predictedData) {
                return;
            }

            packet.setTargetRuntimeID(player.getId());
            if (packet.getType().equals(ActorEvent.FEED)) {
                packet.setData(predictedData);
            }

            player.sendPacket(packet);
            Server.broadcastPacket(player.getViewers().values(), packet);
        }
    }
}
