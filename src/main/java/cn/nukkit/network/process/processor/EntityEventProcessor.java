package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType;
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket;
import org.jetbrains.annotations.NotNull;

public class EntityEventProcessor extends DataPacketProcessor<EntityEventPacket> {
    private static final EntityEventType ADD_PLAYER_LEVELS_EVENT = EntityEventType.PLAYER_ADD_XP_LEVELS;
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EntityEventPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (pk.getType() == EntityEventType.EATING_ITEM) {
            if (pk.getData() == 0 || pk.getRuntimeEntityId() != player.getId()) {
                return;
            }

            Item hand = player.getInventory().getItemInHand();
            if(!(hand instanceof ItemFood)) {
                return;
            }

            int predictedData = (hand.getRuntimeId() << 16) | hand.getDamage();
            if(pk.getData() != predictedData) {
                return;
            }

            pk.setRuntimeEntityId(player.getId());
            pk.setData(predictedData);

            player.dataPacket(pk);
            for (var viewer : player.getViewers().values()) {
                viewer.dataPacket(pk);
            }
        } else if (pk.getType() == ADD_PLAYER_LEVELS_EVENT) {
            if (pk.getRuntimeEntityId() != player.getId()) {
                return;
            }
        }
    }

    @Override
    public Class<EntityEventPacket> getPacketClass() {
        return EntityEventPacket.class;
    }
}
