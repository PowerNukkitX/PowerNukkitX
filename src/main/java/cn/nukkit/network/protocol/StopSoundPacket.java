package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class StopSoundPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.STOP_SOUND_PACKET;

    public String name;
    public boolean stopAll;

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
        byteBuf.writeBoolean(this.stopAll);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
