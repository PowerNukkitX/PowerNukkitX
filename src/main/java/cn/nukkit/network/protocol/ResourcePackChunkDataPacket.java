package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;
import org.powernukkit.version.Version;

import java.util.UUID;

@ToString(exclude = "data")
@PowerNukkitDifference(extendsOnlyInPowerNukkit = AbstractResourcePackDataPacket.class, insteadOf = DataPacket.class, since = "FUTURE")
public class ResourcePackChunkDataPacket extends AbstractResourcePackDataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

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

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public Version getPackVersion() {
        return packVersion;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public void setPackVersion(Version packVersion) {
        this.packVersion = packVersion;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public UUID getPackId() {
        return packId;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    public void setPackId(UUID packId) {
        this.packId = packId;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
