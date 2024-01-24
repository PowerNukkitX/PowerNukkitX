package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import lombok.ToString;


@ToString
public class NetworkSettingsPacket extends DataPacket {

    public int compressionThreshold;
    public PacketCompressionAlgorithm compressionAlgorithm;
    public boolean clientThrottleEnabled;
    public byte clientThrottleThreshold;
    public float clientThrottleScalar;

    @Override
    public int pid() {
        return ProtocolInfo.NETWORK_SETTINGS_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putLShort(this.compressionThreshold);
        this.putLShort(this.compressionAlgorithm.ordinal());
        this.putBoolean(this.clientThrottleEnabled);
        this.putByte(this.clientThrottleThreshold);
        this.putLFloat(this.clientThrottleScalar);
    }

    @Override
    public void decode() {
        this.compressionThreshold = getLShort();
        this.compressionAlgorithm = PacketCompressionAlgorithm.values()[getLShort()];
        this.clientThrottleEnabled = getBoolean();
        this.clientThrottleThreshold = getByte();
        this.clientThrottleScalar = getLFloat();
    }
}
