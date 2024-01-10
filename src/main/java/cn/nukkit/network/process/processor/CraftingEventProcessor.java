package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.CraftingEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class CraftingEventProcessor extends DataPacketProcessor<CraftingEventPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull CraftingEventPacket pk) {
        Player player = playerHandle.player;
        //todo check craft
        /*if (player.craftingType == Player.CRAFTING_BIG && pk.type == CraftingEventPacket.TYPE_WORKBENCH
                || player.craftingType == Player.CRAFTING_SMALL && pk.type == CraftingEventPacket.TYPE_INVENTORY) {
            if (playerHandle.getCraftingTransaction() != null) {
                playerHandle.getCraftingTransaction().setReadyToExecute(true);
                if (playerHandle.getCraftingTransaction().getPrimaryOutput() == null) {
                    playerHandle.getCraftingTransaction().setPrimaryOutput(pk.output[0]);
                }
            }
        }*/
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.CRAFTING_EVENT_PACKET;
    }
}
