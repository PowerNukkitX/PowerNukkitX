package cn.nukkit.network.protocol.types;

import cn.nukkit.utils.BinaryStream;

public interface EventData {
    EventDataType getType();

    void write(BinaryStream stream);
}
