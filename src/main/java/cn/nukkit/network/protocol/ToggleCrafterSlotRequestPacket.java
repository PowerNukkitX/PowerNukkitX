package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ToggleCrafterSlotRequestPacket extends DataPacket {
    public BlockVector3 blockPosition;
    public byte slot;
    public boolean disabled;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.blockPosition = new BlockVector3(byteBuf.readIntLE(), byteBuf.readIntLE(), byteBuf.readIntLE());
        this.slot = byteBuf.readByte();
        this.disabled = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeSignedBlockPosition(this.blockPosition);
        byteBuf.writeByte(this.slot);
        byteBuf.writeBoolean(this.disabled);
    }

    @Override
    public int pid() {
        return ProtocolInfo.TOGGLE_CRAFTER_SLOT_REQUEST;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
