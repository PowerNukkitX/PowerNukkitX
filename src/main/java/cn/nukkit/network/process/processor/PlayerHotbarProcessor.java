package cn.nukkit.network.process.processor;

import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PlayerHotbarPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class PlayerHotbarProcessor extends DataPacketProcessor<PlayerHotbarPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerHotbarPacket pk) {
        if (pk.windowId != ContainerIds.INVENTORY) {
            return; // In PE this should never happen
        }
        playerHandle.player.getInventory().equipItem(pk.selectedHotbarSlot);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.PLAYER_HOTBAR_PACKET);
    }
}
