package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.inventory.request.ItemStackRequestActionProcessor;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ItemStackRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

@Slf4j
public class ItemStackRequestPacketProcessor extends DataPacketProcessor<ItemStackRequestPacket> {
    static final EnumMap<ItemStackRequestActionType, ItemStackRequestActionProcessor<?>> PROCESSORS = new EnumMap<>(ItemStackRequestActionType.class);

    static {

    }

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ItemStackRequestPacket pk) {
        log.info(pk.toString());
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
    }
}
