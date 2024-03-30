package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlockPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.UPDATE_BLOCK_PACKET;
    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;
    public static final int FLAG_ALL = (FLAG_NEIGHBORS | FLAG_NETWORK);
    public static final int FLAG_ALL_PRIORITY = (FLAG_ALL | FLAG_PRIORITY);

    public int x;
    public int z;
    public int y;
    public int blockRuntimeId;
    public int flags;
    public int dataLayer = 0;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
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

        public Entry(int x, int z, int y, String blockId, int blockData, int flags) {
            this.x = x;
            this.z = z;
            this.y = y;
            this.blockId = blockId;
            this.blockData = blockData;
            this.flags = flags;
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
