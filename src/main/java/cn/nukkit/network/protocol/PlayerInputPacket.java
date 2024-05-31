package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInputPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.PLAYER_INPUT_PACKET;

    public float motionX;
    public float motionY;

    public boolean jumping;
    public boolean sneaking;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.motionX = byteBuf.readFloatLE();
        this.motionY = byteBuf.readFloatLE();
        this.jumping = byteBuf.readBoolean();
        this.sneaking = byteBuf.readBoolean();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
