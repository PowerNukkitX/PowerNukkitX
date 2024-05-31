package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlaySoundPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.PLAY_SOUND_PACKET;

    public String name;
    public int x;
    public int y;
    public int z;
    public float volume;
    public float pitch;

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
        
        byteBuf.writeString(this.name);
        byteBuf.writeBlockVector3(this.x * 8, this.y * 8, this.z * 8);
        byteBuf.writeFloatLE(this.volume);
        byteBuf.writeFloatLE(this.pitch);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
