package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class OnScreenTextureAnimationPacket extends DataPacket {

    public int effectId;

    @Override
    public int pid() {
        return ProtocolInfo.ON_SCREEN_TEXTURE_ANIMATION_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.effectId = byteBuf.readIntLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeIntLE(this.effectId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
