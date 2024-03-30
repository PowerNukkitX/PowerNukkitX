package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlockEventPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.BLOCK_EVENT_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int x;
    public int y;
    public int z;
    public int case1;
    public int case2;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.case1);
        byteBuf.writeVarInt(this.case2);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
