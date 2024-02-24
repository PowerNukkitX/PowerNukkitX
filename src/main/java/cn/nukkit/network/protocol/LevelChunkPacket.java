package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString(exclude = "data")
public class LevelChunkPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int chunkX;
    public int chunkZ;
    public int subChunkCount;
    public boolean cacheEnabled;
    public boolean requestSubChunks;
    public int subChunkLimit;
    public long[] blobIds;
    public byte[] data;
    /**
     * @since v649
     */
    public int dimension;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.chunkX);
        byteBuf.writeVarInt(this.chunkZ);
        byteBuf.writeVarInt(this.dimension);
        if (!this.requestSubChunks) {
            byteBuf.writeUnsignedVarInt(this.subChunkCount);
        } else if (this.subChunkLimit < 0) {
            byteBuf.writeUnsignedVarInt(-1);
        } else {
            byteBuf.writeUnsignedVarInt(-2);
            byteBuf.writeUnsignedVarInt(this.subChunkLimit);
        }
        byteBuf.writeBoolean(cacheEnabled);
        if (this.cacheEnabled) {
            byteBuf.writeUnsignedVarInt(blobIds.length);

            for (long blobId : blobIds) {
                byteBuf.writeLongLE(blobId);
            }
        }
        byteBuf.writeByteArray(this.data);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
