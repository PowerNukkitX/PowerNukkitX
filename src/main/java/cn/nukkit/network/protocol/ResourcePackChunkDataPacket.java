package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "data")
public class ResourcePackChunkDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    @Deprecated
    @DeprecationDetails(since = "FUTURE", reason = "The format has been changed from '(uuid of pack)' to '(uuid of pack)_(version of pack)'", replaceWith = "packInfo")
    public UUID packId;
    @PowerNukkitOnly
    @Since("FUTURE")
    public String packInfo;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode() {
        String packInfo = this.getString();
        try {
            this.packId = UUID.fromString(packInfo);
        } catch (IllegalArgumentException exception) {
            this.packInfo = packInfo;
        }
        this.chunkIndex = this.getLInt();
        this.progress = this.getLLong();
        this.data = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        
        if (packInfo == null) {
            this.putString(this.packId.toString());
        } else {
            this.putString(this.packInfo);
        }
        this.putLInt(this.chunkIndex);
        this.putLLong(this.progress);
        this.putByteArray(this.data);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
