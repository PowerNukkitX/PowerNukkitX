package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class PlaySoundPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.PLAY_SOUND_PACKET;

    public String name;
    public int x;
    public int y;
    public int z;
    public float volume;
    public float pitch;

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
        byteBuf.writeBlockVector3(this.x * 8, this.y * 8, this.z * 8);
        byteBuf.writeFloatLE(this.volume);
        byteBuf.writeFloatLE(this.pitch);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
