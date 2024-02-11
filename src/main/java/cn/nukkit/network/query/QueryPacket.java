package cn.nukkit.network.query;

import io.netty.buffer.ByteBuf;

public interface QueryPacket {

    void encode(ByteBuf buf);

    void decode(ByteBuf buf);

    int getSessionId();

    void setSessionId(int sessionId);

    short getId();
}
