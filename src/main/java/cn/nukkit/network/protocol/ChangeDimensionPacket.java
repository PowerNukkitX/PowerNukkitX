package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xtypr
 * @since 2016/1/5
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDimensionPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CHANGE_DIMENSION_PACKET;

    public int dimension;

    public float x;
    public float y;
    public float z;

    public boolean respawn;

    private Integer loadingScreenId = null;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.dimension);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeBoolean(this.respawn);
        byteBuf.writeBoolean(this.loadingScreenId != null);
        if (this.loadingScreenId != null) {
            byteBuf.writeIntLE(this.loadingScreenId);
        }
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
