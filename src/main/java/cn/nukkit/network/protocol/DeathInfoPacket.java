package cn.nukkit.network.protocol;

import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeathInfoPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.DEATH_INFO_PACKET;

    public TranslationContainer translation;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(translation.getText());
        byteBuf.writeArray(translation.getParameters(), byteBuf::writeString);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
