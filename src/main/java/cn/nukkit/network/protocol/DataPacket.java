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
    private int senderSubClientId;
    private int targetSubClientId;

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

    @Override
    public DataPacket reset() {
        super.reset();
        this.putUnsignedVarInt(this.packetId());
        return this;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return channel;
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

    public BatchPacket compress() {
        return compress(Server.getInstance().networkCompressionLevel);
    }

    public BatchPacket compress(int level) {
        BatchPacket batch = new BatchPacket();
        byte[][] batchPayload = new byte[2][];
        byte[] buf = getBuffer();
        batchPayload[0] = Binary.writeUnsignedVarInt(buf.length);
        batchPayload[1] = buf;
        try {
            batch.payload = Network.deflateRaw(batchPayload, level);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return batch;
    }

    public int getTargetSubClientId() {
        return targetSubClientId;
    }

    public void setTargetSubClientId(int targetSubClientId) {
        this.targetSubClientId = targetSubClientId;
    }

    public int getSenderSubClientId() {
        return senderSubClientId;
    }

    public void setSenderSubClientId(int senderSubClientId) {
        this.senderSubClientId = senderSubClientId;
    }
}
