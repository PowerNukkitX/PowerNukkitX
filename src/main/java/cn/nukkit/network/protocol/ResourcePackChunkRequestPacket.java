package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackChunkRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;

    @Deprecated
    public UUID packId;
    @PowerNukkitOnly
    @Since("FUTURE")
    public String packInfo;
    public int chunkIndex;

    @Override
    public void decode() {
        String packInfo = this.getString();
        try {
            this.packId = UUID.fromString(packInfo);
        } catch (IllegalArgumentException exception) {
            this.packInfo = packInfo;
        }
        this.chunkIndex = this.getLInt();
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
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
