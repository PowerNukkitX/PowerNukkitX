package cn.nukkit.network.protocol;

import cn.nukkit.utils.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString
public class PlayerFogPacket extends DataPacket {

    /**
     * Fog stack containing fog effects from the /fog command
     */
    @Getter
    @Setter
    private List<Fog> fogStack = new ArrayList<>();

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_FOG_PACKET;
    }

    @Override
    public void decode() {
        //unused
    }

    @Override
    public void encode() {
        this.reset();
        this.putArray(fogStack, fog -> this.putString(fog.identifier().toString()));
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
