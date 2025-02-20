package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

/**
 * @author Pub4Game
 * @since 03.07.2016
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemFrameDropItemPacket extends DataPacket {
    public int x;
    public int y;
    public int z;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        BlockVector3 v = byteBuf.readBlockVector3();
        this.z = v.z;
        this.y = v.y;
        this.x = v.x;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
