package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.lang.TranslationContainer;


public class DeathInfoPacket extends DataPacket{

    public static final byte NETWORK_ID = ProtocolInfo.DEATH_INFO_PACKET;

    public TranslationContainer translation;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        //empty
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(translation.getText());
        this.putArray(translation.getParameters(), this::putString);
    }
}
