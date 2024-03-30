package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.Identifier;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerFogPacket extends DataPacket {

    /**
     * Fog stack containing fog effects from the /fog command
     */
    public List<Fog> fogStack = new ArrayList<>();

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_FOG_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //unused
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(fogStack, fog -> byteBuf.writeString(fog.identifier().toString()));
    }

    /**
     * @param identifier 这个迷雾的命名空间id
     * @param userProvidedId 用户指定的特征id
     */
    public record Fog(Identifier identifier, String userProvidedId){

    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
