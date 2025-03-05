package cn.nukkit.network.protocol;

import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeathInfoPacket extends DataPacket {
    public TranslationContainer translation;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(translation.getText());
        byteBuf.writeArray(translation.getParameters(), byteBuf::writeString);
    }

    @Override
    public int pid() {
        return ProtocolInfo.DEATH_INFO_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
