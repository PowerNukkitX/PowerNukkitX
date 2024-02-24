package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class NetworkChunkPublisherUpdatePacket extends DataPacket {

    public BlockVector3 position;
    public int radius;

    @Override
    public int pid() {
        return ProtocolInfo.NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.position = byteBuf.readSignedBlockPosition();
        this.radius = (int) byteBuf.readUnsignedVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeSignedBlockPosition(position);
        byteBuf.writeUnsignedVarInt(radius);
        byteBuf.writeInt(0); // Saved chunks
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
