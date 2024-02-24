package cn.nukkit.network.protocol.types;

import cn.nukkit.network.connection.util.HandleByteBuf;

public interface EventData {
    EventDataType getType();

    void write(HandleByteBuf byteBuf);
}
