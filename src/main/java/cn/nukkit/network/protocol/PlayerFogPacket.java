package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.Identifier;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerFogPacket extends DataPacket {
    //Fog stack containing fog effects from the /fog command
    public List<Fog> fogStack = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(fogStack, fog -> byteBuf.writeString(fog.identifier().toString()));
    }

    /**
     * @param identifier The namespace id of this fog
     * @param userProvidedId User-specified feature id
     */
    public record Fog(Identifier identifier, String userProvidedId){

    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_FOG_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
