package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString
@NoArgsConstructor
public class ItemStackRequestPacket extends DataPacket {
    public final List<ItemStackRequest> requests = new ArrayList<>();

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        requests.addAll(List.of(byteBuf.readArray(ItemStackRequest.class, HandleByteBuf::readItemStackRequest)));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        //non server bound
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
