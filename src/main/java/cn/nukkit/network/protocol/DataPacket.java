package cn.nukkit.network.protocol;

import cn.nukkit.utils.BinaryStream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class DataPacket extends BinaryStream {
    public static final DataPacket[] EMPTY_ARRAY = new DataPacket[0];
    public volatile boolean isEncoded = false;

    public abstract int pid();

    /**
     * @return The protocol version of the packet. If it is lower than CURRENT_PROTOCOL, pnx will try to translate it.
     */
    public int getProtocolUsed() {
        return ProtocolInfo.CURRENT_PROTOCOL;
    }

    public abstract void decode();

    public abstract void encode();

    public final void tryEncode() {
        if (!this.isEncoded) {
            this.isEncoded = true;
            this.encode();
        }
    }
}
