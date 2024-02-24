package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class SetLastHurtByPacket extends DataPacket {

    @Override
    public int pid() {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        //TODO
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
