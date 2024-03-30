package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowProfilePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SHOW_PROFILE_PACKET;

    public String xuid;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.xuid = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(this.xuid);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
