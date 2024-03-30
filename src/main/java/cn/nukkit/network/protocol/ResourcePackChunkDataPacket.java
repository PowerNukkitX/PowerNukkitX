package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.version.Version;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "data")
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePackChunkDataPacket
 extends AbstractResourcePackDataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public UUID packId;
    private Version packVersion;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        decodePackInfo(byteBuf);
        this.chunkIndex = byteBuf.readIntLE();
        this.progress = byteBuf.readLongLE();
        this.data = byteBuf.readByteArray();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        encodePackInfo(byteBuf);
        byteBuf.writeIntLE(this.chunkIndex);
        byteBuf.writeLongLE(this.progress);
        byteBuf.writeByteArray(this.data);
    }

    @Override
    public Version getPackVersion() {
        return packVersion;
    }

    @Override
    public void setPackVersion(Version packVersion) {
        this.packVersion = packVersion;
    }

    @Override
    public UUID getPackId() {
        return packId;
    }

    @Override
    public void setPackId(UUID packId) {
        this.packId = packId;
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
