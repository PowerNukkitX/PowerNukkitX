package cn.nukkit.network.protocol;

import javax.annotation.Nonnegative;


public class AgentAnimationPacket extends DataPacket {
    public byte animation;
    public long runtimeEntityId;

    @Override
    public int pid() {
        return ProtocolInfo.AGENT_ANIMATION;
    }

    @Override
    public void decode() {
        this.animation = getByte();
        this.runtimeEntityId = getEntityRuntimeId();
    }

    @Override
    public void encode() {
        putByte(this.animation);
        putEntityRuntimeId(this.runtimeEntityId);
    }
}
