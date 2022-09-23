package cn.nukkit.network.protocol;


import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class RequestNetworkSettingsPacket extends DataPacket {

    public int protocolVersion;

    @Override
    public byte pid() {
        return ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;
    }

    @Override
    public void encode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode() {
        this.protocolVersion = this.getInt();
    }
}
