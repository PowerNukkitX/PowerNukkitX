package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;
import org.powernukkit.version.Version;

import java.util.UUID;

@ToString(exclude = "sha256")
@PowerNukkitDifference(extendsOnlyInPowerNukkit = AbstractResourcePackDataPacket.class, insteadOf = DataPacket.class, since = "1.5.2.0-PN")
public class ResourcePackDataInfoPacket extends AbstractResourcePackDataPacket {

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
        decodePackInfo();
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
        encodePackInfo();
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

    @Override
    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public Version getPackVersion() {
        return packVersion;
    }

    @Override
    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setPackVersion(Version packVersion) {
        this.packVersion = packVersion;
    }

    @Since("1.5.2.0-PN")
    @PowerNukkitOnly
    @Override
    public UUID getPackId() {
        return packId;
    }

    @Since("1.5.2.0-PN")
    @PowerNukkitOnly
    @Override
    public void setPackId(UUID packId) {
        this.packId = packId;
    }
}
