package cn.nukkit.network.protocol;

import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeathInfoPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.DEATH_INFO_PACKET;

    public TranslationContainer translation;

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
        //empty
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(translation.getText());
        byteBuf.writeArray(translation.getParameters(), byteBuf::writeString);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
