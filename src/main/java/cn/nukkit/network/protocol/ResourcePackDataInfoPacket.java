package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.version.Version;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "sha256")
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePackDataInfoPacket
 extends AbstractResourcePackDataPacket {

    public static final int $1 = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;
    public static final int $6 = 4;
    public static final int $7 = 5;
    public static final int $8 = 6;
    public static final int $9 = 7;
    public static final int $10 = 8;
    public static final int $11 = 9;

    public UUID packId;
    private Version packVersion;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;
    public boolean premium;
    public int $12 = TYPE_RESOURCE;

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public Version getPackVersion() {
        return packVersion;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setPackVersion(Version packVersion) {
        this.packVersion = packVersion;
    }

    @Override
    public UUID getPackId() {
        return packId;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setPackId(UUID packId) {
        this.packId = packId;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
