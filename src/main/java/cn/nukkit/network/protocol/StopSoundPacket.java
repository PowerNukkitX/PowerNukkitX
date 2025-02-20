package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StopSoundPacket extends DataPacket {
    public String name;
    public boolean stopAll;
    public boolean stopMusicLegacy;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.name);
        byteBuf.writeBoolean(this.stopAll);
        byteBuf.writeBoolean(this.stopMusicLegacy);
    }

    @Override
    public int pid() {
        return ProtocolInfo.STOP_SOUND_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
