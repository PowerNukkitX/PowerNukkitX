package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class PlayerVideoCapturePacket extends DataPacket {
    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_VIDEO_CAPTURE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
