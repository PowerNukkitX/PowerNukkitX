package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PacketViolationWarningPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;
    public PacketViolationType type;
    public PacketViolationSeverity severity;
    public int packetId;
    public String context;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.type = PacketViolationType.values()[byteBuf.readVarInt() + 1];
        this.severity = PacketViolationSeverity.values()[byteBuf.readVarInt()];
        this.packetId = byteBuf.readVarInt();
        this.context = byteBuf.readString();
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
