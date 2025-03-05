package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemStackResponsePacket extends DataPacket {
    public final List<ItemStackResponse> entries = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(entries, (r) -> {
            byteBuf.writeByte((byte) r.getResult().ordinal());
            byteBuf.writeVarInt(r.getRequestId());
            if (r.getResult() != ItemStackResponseStatus.OK) return;
            byteBuf.writeArray(r.getContainers(), (container) -> {
                byteBuf.writeFullContainerName(container.getContainerName());
                byteBuf.writeArray(container.getItems(), (item) -> {
                    byteBuf.writeByte((byte) item.getSlot());
                    byteBuf.writeByte((byte) item.getHotbarSlot());
                    byteBuf.writeByte((byte) item.getCount());
                    byteBuf.writeVarInt(item.getStackNetworkId());
                    byteBuf.writeString(item.getCustomName());
                    byteBuf.writeString(item.getFilteredCustomName());
                    byteBuf.writeVarInt(item.getDurabilityCorrection());
                });
            });
        });
    }

    @Override
    public int pid() {
        return ProtocolInfo.ITEM_STACK_RESPONSE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
