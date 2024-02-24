package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class AddBehaviorTreePacket extends DataPacket {
    public String behaviorTreeJson;

    @Override
    public int pid() {
        return ProtocolInfo.ADD_BEHAVIOR_TREE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(behaviorTreeJson);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
