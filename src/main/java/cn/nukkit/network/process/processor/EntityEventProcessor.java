package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class EntityEventProcessor extends DataPacketProcessor<EntityEventPacket> {
    private static final int ADD_PLAYER_LEVELS_EVENT = EntityEventPacket.DEPRECATED_ADD_PLAYER_LEVELS;
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EntityEventPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (pk.event == EntityEventPacket.EATING_ITEM) {
            if (pk.data == 0 || pk.eid != player.getId()) {
                return;
            }

            Item hand = player.getInventory().getItemInHand();
            if(!(hand instanceof ItemFood)) {
                return;
            }

            int predictedData = (hand.getRuntimeId() << 16) | hand.getDamage();
            if(pk.data != predictedData) {
                return;
            }

            pk.eid = player.getId();
            pk.data = predictedData;

            player.dataPacket(pk);
            Server.broadcastPacket(player.getViewers().values(), pk);
        } else if (pk.event == ADD_PLAYER_LEVELS_EVENT) {
            if (pk.eid != player.getId()) {
                return;
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ENTITY_EVENT_PACKET;
    }
}
