package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetCommandsEnabledPacket extends DataPacket {
    public boolean enabled;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.enabled);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_COMMANDS_ENABLED_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
