package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemStackRequestPacket extends DataPacket {
    public final List<ItemStackRequest> requests = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        requests.addAll(List.of(byteBuf.readArray(ItemStackRequest.class, HandleByteBuf::readItemStackRequest)));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
