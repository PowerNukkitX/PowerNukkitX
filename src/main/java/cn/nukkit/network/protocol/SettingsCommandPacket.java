package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SettingsCommandPacket extends DataPacket {
    public String command;
    public boolean suppressOutput;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.command = byteBuf.readString();
        this.suppressOutput = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(command);
        byteBuf.writeBoolean(suppressOutput);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SETTINGS_COMMAND_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
