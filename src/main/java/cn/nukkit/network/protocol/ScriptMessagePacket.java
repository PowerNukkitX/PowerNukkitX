package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScriptMessagePacket extends DataPacket {
    private String channel;
    private String message;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.SCRIPT_MESSAGE_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.channel = byteBuf.readString();
        this.message = byteBuf.readString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(channel);
        byteBuf.writeString(message);
    }
    /**
     * @deprecated 
     */
    

    public String _getChannel() {
        return channel;
    }
    /**
     * @deprecated 
     */
    

    public void setChannel(String channel) {
        this.channel = channel;
    }
    /**
     * @deprecated 
     */
    

    public String getMessage() {
        return message;
    }
    /**
     * @deprecated 
     */
    

    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
