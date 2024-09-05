package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransferPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String address; // Server address
    public int port = 19132; // Server port
    private boolean reloadWorld;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.address = byteBuf.readString();
        this.port = byteBuf.readShortLE();
        this.reloadWorld = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(address);
        byteBuf.writeShortLE(port);
        byteBuf.writeBoolean(this.reloadWorld);
    }

    @Override
    public int pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
