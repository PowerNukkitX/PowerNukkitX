package cn.nukkit.network.protocol;

import lombok.ToString;
import cn.nukkit.utils.version.Version;

import java.util.UUID;

@ToString(exclude = "data")

public class ResourcePackChunkDataPacket extends AbstractResourcePackDataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public UUID packId;
    private Version packVersion;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode() {
        decodePackInfo();
        this.chunkIndex = this.getLInt();
        this.progress = this.getLLong();
        this.data = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        encodePackInfo();
        this.putLInt(this.chunkIndex);
        this.putLLong(this.progress);
        this.putByteArray(this.data);
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
}
