package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class EntityEventProcessor extends DataPacketProcessor<EntityEventPacket> {
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

            pk.eid = player.getId();

            player.dataPacket(pk);
            Server.broadcastPacket(player.getViewers().values(), pk);
        } else if (pk.event == EntityEventPacket.ENCHANT) {
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
