package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;
import org.powernukkit.version.Version;

import java.util.UUID;

@ToString(exclude = "sha256")
public class ResourcePackDataInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;

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
    public void decode() {
        String packInfo = this.getString();
        String[] packInfoParts = packInfo.split("_", 2);
        try {
            this.packId = UUID.fromString(packInfoParts[0]);
        } catch (IllegalArgumentException exception) {
            this.packId = null;
        }
        this.packVersion = (packInfoParts.length > 1)? new Version(packInfoParts[1]) : null;
        this.maxChunkSize = this.getLInt();
        this.chunkCount = this.getLInt();
        this.compressedPackSize = this.getLLong();
        this.sha256 = this.getByteArray();
        this.premium = this.getBoolean();
        this.type = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        String packInfo = (packId != null) ? packId.toString() : new UUID(0, 0).toString();
        if (packVersion != null) {
            packInfo += "_" + packVersion;
        }
        this.putString(packInfo);
        this.putLInt(this.maxChunkSize);
        this.putLInt(this.chunkCount);
        this.putLLong(this.compressedPackSize);
        this.putByteArray(this.sha256);
        this.putBoolean(this.premium);
        this.putByte((byte) this.type);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Version getPackVersion() {
        return packVersion;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setPackVersion(Version packVersion) {
        this.packVersion = packVersion;
    }
}
