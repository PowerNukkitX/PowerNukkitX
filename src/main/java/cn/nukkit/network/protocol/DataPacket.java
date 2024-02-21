package cn.nukkit.network.protocol;

import cn.nukkit.utils.BinaryStream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class DataPacket extends BinaryStream {
    public static final DataPacket[] EMPTY_ARRAY = new DataPacket[0];
    public volatile boolean isEncoded = false;

    public abstract int pid();

    public abstract void decode();

    public abstract void encode();

    public final void tryEncode() {
        if (!this.isEncoded) {
            this.isEncoded = true;
            this.encode();
        }
    }

    public abstract void handle(PacketHandler handler);
}
