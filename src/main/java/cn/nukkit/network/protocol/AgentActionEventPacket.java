package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.AgentActionType;

/**
 * @since v503
 */
public class AgentActionEventPacket extends DataPacket {
    private String requestId;
    private AgentActionType actionType;
    /**
     * @see AgentActionType for type specific JSON
     */
    private String responseJson;

    @Override
    public byte pid() {
        return ProtocolInfo.AGENT_ACTION_EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.requestId = getString();
        this.actionType = AgentActionType.values()[getByte()];
        this.responseJson = getString();
    }

    @Override
    public void encode() {
        putString(this.requestId);
        putByte((byte) actionType.ordinal());
        putString(responseJson);
    }
}
