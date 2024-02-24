package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.AgentActionType;

/**
 * @since v503
 */
public class AgentActionEventPacket extends DataPacket {
    public String requestId;
    public AgentActionType actionType;
    /**
     * @see AgentActionType for type specific JSON
     */
    public String responseJson;

    @Override
    public int pid() {
        return ProtocolInfo.AGENT_ACTION_EVENT_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.requestId = byteBuf.readString();
        this.actionType = AgentActionType.values()[byteBuf.readByte()];
        this.responseJson = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.requestId);
        byteBuf.writeByte((byte) actionType.ordinal());
        byteBuf.writeString(responseJson);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
