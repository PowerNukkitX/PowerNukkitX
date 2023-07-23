package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nonnegative;

@Since("1.20.10-r1")
@PowerNukkitXOnly
public class AgentAnimationPacket extends DataPacket {
    public byte animation;
    public long runtimeEntityId;

    @Override
    public byte pid() {
        return (byte) ProtocolInfo.AGENT_ANIMATION;
    }

    @Nonnegative
    @Since("1.19.70-r1")
    @Override
    public int packetId() {
        return ProtocolInfo.AGENT_ANIMATION;
    }

    @Override
    public void decode() {
        this.animation = (byte) getByte();
        this.runtimeEntityId = getEntityRuntimeId();
    }

    @Override
    public void encode() {
        putByte(this.animation);
        putEntityRuntimeId(this.runtimeEntityId);
    }
}
