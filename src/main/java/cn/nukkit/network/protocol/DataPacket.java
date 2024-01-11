package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.network.Network;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;

import javax.annotation.Nonnegative;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class DataPacket extends BinaryStream implements Cloneable {
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

    public DataPacket clean() {
        this.setBuffer(null);
        this.setOffset(0);
        this.isEncoded = false;
        return this;
    }

    @Override
    public DataPacket clone() {
        try {
            DataPacket packet = (DataPacket) super.clone();
            packet.setBuffer(this.getBuffer()); // prevent reflecting same buffer instance
            packet.offset = this.offset;
            packet.count = this.count;
            return packet;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
