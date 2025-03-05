package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LecternUpdatePacket extends DataPacket {
    public int page;
    public int totalPages;
    public BlockVector3 blockPosition;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.page = byteBuf.readUnsignedByte();
        this.totalPages = byteBuf.readUnsignedByte();
        this.blockPosition = byteBuf.readBlockVector3();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.LECTERN_UPDATE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
