package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r3")
@ToString
public class PlayerFogPacket extends DataPacket {

    /**
     * Fog stack containing fog effects from the /fog command
     */
    @Getter
    @Setter
    private List<Fog> fogStack = new ArrayList<>();

    @Override
    public byte pid() {
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
}
