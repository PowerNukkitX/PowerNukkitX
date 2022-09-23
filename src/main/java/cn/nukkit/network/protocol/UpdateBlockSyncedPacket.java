package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.BlockSyncType;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class UpdateBlockSyncedPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_BLOCK_SYNCED_PACKET;
    public long actorUniqueId;
    public BlockSyncType updateType;


    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarLong(actorUniqueId);
        this.putUnsignedVarLong(updateType.ordinal());
    }
}
