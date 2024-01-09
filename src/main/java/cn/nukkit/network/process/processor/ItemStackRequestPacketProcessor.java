package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.BlockPickRequestPacket;
import cn.nukkit.network.protocol.ItemStackRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ItemStackRequestPacketProcessor extends DataPacketProcessor<ItemStackRequestPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ItemStackRequestPacket pk) {
        log.info(pk.toString());
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
    }
}
