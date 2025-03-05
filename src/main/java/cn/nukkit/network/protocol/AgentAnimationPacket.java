package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentAnimationPacket extends DataPacket {
    public byte animation;
    public long runtimeEntityId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.animation = byteBuf.readByte();
        this.runtimeEntityId = byteBuf.readEntityRuntimeId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(this.animation);
        byteBuf.writeEntityRuntimeId(this.runtimeEntityId);
    }

    @Override
    public int pid() {
        return ProtocolInfo.AGENT_ANIMATION;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
