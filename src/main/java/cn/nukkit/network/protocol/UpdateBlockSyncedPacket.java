package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.BlockSyncType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlockSyncedPacket extends UpdateBlockPacket {
    public static final int $1 = ProtocolInfo.UPDATE_BLOCK_SYNCED_PACKET;
    public long actorUniqueId;
    public BlockSyncType updateType;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        super.encode(byteBuf);
        byteBuf.writeUnsignedVarLong(actorUniqueId);
        byteBuf.writeUnsignedVarLong(updateType.ordinal());
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
