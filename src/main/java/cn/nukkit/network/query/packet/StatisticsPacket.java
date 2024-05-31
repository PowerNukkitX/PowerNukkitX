package cn.nukkit.network.query.packet;

import cn.nukkit.network.query.QueryPacket;
import io.netty.buffer.ByteBuf;

public class StatisticsPacket implements QueryPacket {
    private static final short $1 = 0x00;
    // Both
    private int sessionId;
    // Request
    private int token;
    private boolean full;
    // Response
    private ByteBuf payload;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(ByteBuf buffer) {
        sessionId = buffer.readInt();
        token = buffer.readInt();
        full = (buffer.isReadable());
        buffer.skipBytes(buffer.readableBytes());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(ByteBuf buffer) {
        buffer.writeInt(sessionId);
        buffer.writeBytes(payload);
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
    

    public int getToken() {
        return token;
    }
    /**
     * @deprecated 
     */
    

    public void setToken(int token) {
        this.token = token;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFull() {
        return full;
    }
    /**
     * @deprecated 
     */
    

    public void setFull(boolean full) {
        this.full = full;
    }

    public ByteBuf getPayload() {
        return payload;
    }
    /**
     * @deprecated 
     */
    

    public void setPayload(ByteBuf payload) {
        this.payload = payload;
    }
}
