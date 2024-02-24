package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class DataPacket {
    public static final DataPacket[] EMPTY_ARRAY = new DataPacket[0];

    public abstract int pid();

    public abstract void decode(HandleByteBuf byteBuf);

    public abstract void encode(HandleByteBuf byteBuf);

    public abstract void handle(PacketHandler handler);
}
