package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowProfilePacket extends DataPacket {
    public String xuid;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.xuid = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.xuid);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SHOW_PROFILE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
