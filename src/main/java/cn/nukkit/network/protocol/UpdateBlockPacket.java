package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlockPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.UPDATE_BLOCK_PACKET;
    public static final int $2 = 0b0000;
    public static final int $3 = 0b0001;
    public static final int $4 = 0b0010;
    public static final int $5 = 0b0100;
    public static final int $6 = 0b1000;
    public static final int $7 = (FLAG_NEIGHBORS | FLAG_NETWORK);
    public static final int $8 = (FLAG_ALL | FLAG_PRIORITY);

    public int x;
    public int z;
    public int y;
    public int blockRuntimeId;
    public int flags;
    public int $9 = 0;

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
    
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBlockVector3(x, y, z);
        byteBuf.writeUnsignedVarInt(blockRuntimeId);
        byteBuf.writeUnsignedVarInt(flags);
        byteBuf.writeUnsignedVarInt(dataLayer);
    }

    public static class Entry {
        public final int x;
        public final int z;
        public final int y;
        public final String blockId;
        public final int blockData;
        public final int flags;
    /**
     * @deprecated 
     */
    

        public Entry(int x, int z, int y, String blockId, int blockData, int flags) {
            this.x = x;
            this.z = z;
            this.y = y;
            this.blockId = blockId;
            this.blockData = blockData;
            this.flags = flags;
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
