package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @since 15-10-14
 */
@ToString
public class TakeItemEntityPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;

    public long entityId;
    public long target;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.target = byteBuf.readEntityRuntimeId();
        this.entityId = byteBuf.readEntityRuntimeId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityRuntimeId(this.target);
        byteBuf.writeEntityRuntimeId(this.entityId);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
