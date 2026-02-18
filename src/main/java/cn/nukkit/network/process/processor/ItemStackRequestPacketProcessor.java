package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponse;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseStatus;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackResponsePacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemStackRequestPacketProcessor extends DataPacketProcessor<ItemStackRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ItemStackRequestPacket pk) {
        var responsePacket = new ItemStackResponsePacket();
        for (var request : pk.getRequests()) {
            responsePacket.getEntries().add(new ItemStackResponse(
                    ItemStackResponseStatus.ERROR,
                    request.getRequestId(),
                    new ArrayList<>()
            ));
        }
        playerHandle.player.dataPacket(responsePacket);
    }

    @Override
    public Class<ItemStackRequestPacket> getPacketClass() {
        return ItemStackRequestPacket.class;
    }
}
