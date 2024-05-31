package cn.nukkit.network.query.packet;

import cn.nukkit.network.query.QueryPacket;
import cn.nukkit.network.query.QueryUtil;
import io.netty.buffer.ByteBuf;

public class HandshakePacket implements QueryPacket {
    private static final short $1 = 0x09;
    // Both
    private int sessionId;
    // Response
    private String token;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(ByteBuf buffer) {
        sessionId = buffer.readInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(ByteBuf buffer) {
        buffer.writeInt(sessionId);
        QueryUtil.writeNullTerminatedString(buffer, token);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public short getId() {
        return ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getSessionId() {
        return sessionId;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    /**
     * @deprecated 
     */
    

    public String getToken() {
        return token;
    }
    /**
     * @deprecated 
     */
    

    public void setToken(String token) {
        this.token = token;
    }
}
