package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;


@ToString
public class PacketViolationWarningPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;
    public PacketViolationType type;
    public PacketViolationSeverity severity;
    public int packetId;
    public String context;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.type = PacketViolationType.values()[byteBuf.readVarInt() + 1];
        this.severity = PacketViolationSeverity.values()[byteBuf.readVarInt()];
        this.packetId = byteBuf.readVarInt();
        this.context = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(this.type.ordinal() - 1);
        byteBuf.writeVarInt(this.severity.ordinal());
        byteBuf.writeVarInt(this.packetId);
        byteBuf.writeString(this.context);
    }

    public enum PacketViolationType {
        UNKNOWN,
        MALFORMED_PACKET
    }

    public enum PacketViolationSeverity {
        UNKNOWN,
        WARNING,
        FINAL_WARNING,
        TERMINATING_CONNECTION
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
