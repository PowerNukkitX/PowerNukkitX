package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.BlockSyncType;
import lombok.ToString;


@ToString
public class UpdateBlockSyncedPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_BLOCK_SYNCED_PACKET;
    public long actorUniqueId;
    public BlockSyncType updateType;


    @Override
    public int pid() {
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
