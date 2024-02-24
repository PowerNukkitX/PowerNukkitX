package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;
import cn.nukkit.utils.version.Version;

import java.util.UUID;

@ToString(exclude = "sha256")

public class ResourcePackDataInfoPacket extends AbstractResourcePackDataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;

    public static final int TYPE_INVALID = 0;
    public static final int TYPE_ADDON = 1;
    public static final int TYPE_CACHED = 2;
    public static final int TYPE_COPY_PROTECTED = 3;
    public static final int TYPE_BEHAVIOR = 4;
    public static final int TYPE_PERSONA_PIECE = 5;
    public static final int TYPE_RESOURCE = 6;
    public static final int TYPE_SKINS = 7;
    public static final int TYPE_WORLD_TEMPLATE = 8;
    public static final int TYPE_COUNT = 9;

    public UUID packId;
    private Version packVersion;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;
    public boolean premium;
    public int type = TYPE_RESOURCE;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        decodePackInfo(byteBuf);
        this.maxChunkSize = byteBuf.readIntLE();
        this.chunkCount = byteBuf.readIntLE();
        this.compressedPackSize = byteBuf.readLongLE();
        this.sha256 = byteBuf.readByteArray();
        this.premium = byteBuf.readBoolean();
        this.type = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        encodePackInfo(byteBuf);
        byteBuf.writeIntLE(this.maxChunkSize);
        byteBuf.writeIntLE(this.chunkCount);
        byteBuf.writeLongLE(this.compressedPackSize);
        byteBuf.writeByteArray(this.sha256);
        byteBuf.writeBoolean(this.premium);
        byteBuf.writeByte((byte) this.type);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
