package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GUIDataPickItemPacket extends DataPacket {
    public int hotbarSlot;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.hotbarSlot = byteBuf.readIntLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(this.hotbarSlot);
    }

    @Override
    public int pid() {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
