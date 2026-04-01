package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EntityPickRequestPacket extends DataPacket {

    private long entityId;
    private int maxSlots;
    private boolean withData;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.setEntityId(byteBuf.readLongLE());
        this.setMaxSlots(byteBuf.readUnsignedByte());
        this.setWithData(byteBuf.readBoolean());
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeLongLE(this.entityId);
        byteBuf.writeByte(this.maxSlots);
        byteBuf.writeBoolean(this.withData);
    }

    @Override
    public int pid() {
        return ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}