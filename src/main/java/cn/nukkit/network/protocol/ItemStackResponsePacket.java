package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseStatus;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@NoArgsConstructor
public class ItemStackResponsePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ITEM_STACK_RESPONSE_PACKET;

    public final List<ItemStackResponse> entries = new ArrayList<>();

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(entries, (r) -> {
            byteBuf.writeByte((byte) r.getResult().ordinal());
            byteBuf.writeVarInt(r.getRequestId());
            if (r.getResult() != ItemStackResponseStatus.OK) return;
            byteBuf.writeArray(r.getContainers(), (container) -> {
                byteBuf.writeByte((byte) container.getContainer().getId());
                byteBuf.writeArray(container.getItems(), (item) -> {
                    byteBuf.writeByte((byte) item.getSlot());
                    byteBuf.writeByte((byte) item.getHotbarSlot());
                    byteBuf.writeByte((byte) item.getCount());
                    byteBuf.writeVarInt(item.getStackNetworkId());
                    byteBuf.writeString(item.getCustomName());
                    byteBuf.writeVarInt(item.getDurabilityCorrection());
                });
            });
        });
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();//client bound
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
