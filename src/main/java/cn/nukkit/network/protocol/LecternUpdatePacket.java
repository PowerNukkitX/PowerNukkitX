package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class LecternUpdatePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.LECTERN_UPDATE_PACKET;

    public int page;
    public int totalPages;
    public BlockVector3 blockPosition;
    public boolean dropBook;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.page = byteBuf.readByte();
        this.totalPages = byteBuf.readByte();
        this.blockPosition = byteBuf.readBlockVector3();
        this.dropBook = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
