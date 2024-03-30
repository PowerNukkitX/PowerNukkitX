package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LecternUpdatePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.LECTERN_UPDATE_PACKET;

    public int page;
    public int totalPages;
    public BlockVector3 blockPosition;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.page = byteBuf.readUnsignedByte();
        this.totalPages = byteBuf.readUnsignedByte();
        this.blockPosition = byteBuf.readBlockVector3();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
