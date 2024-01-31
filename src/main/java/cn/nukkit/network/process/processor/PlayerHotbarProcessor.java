package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PlayerHotbarPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class PlayerHotbarProcessor extends DataPacketProcessor<PlayerHotbarPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerHotbarPacket pk) {
        if (pk.windowId != SpecialWindowId.PLAYER.getId()) {
            return; //In PE this should never happen
        }
        playerHandle.player.getInventory().equipItem(pk.selectedHotbarSlot);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET;
    }
}
