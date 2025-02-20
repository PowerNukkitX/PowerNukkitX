package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlockPickRequestPacket extends DataPacket {
    public int x;
    public int y;
    public int z;
    public boolean addUserData;
    public int selectedSlot;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        BlockVector3 v = byteBuf.readSignedBlockPosition();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.addUserData = byteBuf.readBoolean();
        this.selectedSlot = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.BLOCK_PICK_REQUEST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
