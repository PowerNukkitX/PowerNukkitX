package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class StopSoundPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.STOP_SOUND_PACKET;

    public String name;
    public boolean stopAll;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(this.name);
        byteBuf.writeBoolean(this.stopAll);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
