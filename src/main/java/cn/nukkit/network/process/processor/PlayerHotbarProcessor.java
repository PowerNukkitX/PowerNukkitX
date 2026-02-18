package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;
import org.jetbrains.annotations.NotNull;

public class PlayerHotbarProcessor extends DataPacketProcessor<PlayerHotbarPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerHotbarPacket pk) {
        if (pk.getContainerId() != SpecialWindowId.PLAYER.getId()) {
            return; //In PE this should never happen
        }
        playerHandle.player.getInventory().equipItem(pk.getSelectedHotbarSlot());
    }
    @Override
    public Class<PlayerHotbarPacket> getPacketClass() {
        return PlayerHotbarPacket.class;
    }
}
