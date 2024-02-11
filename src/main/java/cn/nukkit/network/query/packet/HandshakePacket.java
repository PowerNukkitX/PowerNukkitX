package cn.nukkit.network.query.packet;

import cn.nukkit.network.query.QueryPacket;
import cn.nukkit.network.query.QueryUtil;
import io.netty.buffer.ByteBuf;

public class HandshakePacket implements QueryPacket {
    private static final short ID = 0x09;
    // Both
    private int sessionId;
    // Response
    private String token;

    @Override
    public void decode(ByteBuf buffer) {
        sessionId = buffer.readInt();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(sessionId);
        QueryUtil.writeNullTerminatedString(buffer, token);
    }

    @Override
    public short getId() {
        return ID;
    }

    @Override
    public int getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
