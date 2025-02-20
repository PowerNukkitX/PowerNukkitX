package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NetworkSettingsPacket extends DataPacket {
    public int compressionThreshold;
    public PacketCompressionAlgorithm compressionAlgorithm;
    public boolean clientThrottleEnabled;
    public byte clientThrottleThreshold;
    public float clientThrottleScalar;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeShortLE(this.compressionThreshold);
        byteBuf.writeShortLE(this.compressionAlgorithm.ordinal());
        byteBuf.writeBoolean(this.clientThrottleEnabled);
        byteBuf.writeByte(this.clientThrottleThreshold);
        byteBuf.writeFloatLE(this.clientThrottleScalar);
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.compressionThreshold = byteBuf.readShortLE();
        this.compressionAlgorithm = PacketCompressionAlgorithm.values()[byteBuf.readShortLE()];
        this.clientThrottleEnabled = byteBuf.readBoolean();
        this.clientThrottleThreshold = byteBuf.readByte();
        this.clientThrottleScalar = byteBuf.readFloatLE();
    }

    @Override
    public int pid() {
        return ProtocolInfo.NETWORK_SETTINGS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
