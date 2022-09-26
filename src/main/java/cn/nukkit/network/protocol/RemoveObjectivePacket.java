package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.30-r1")
public class RemoveObjectivePacket extends DataPacket {

    public String objectiveName;

    @Override
    public byte pid() {
        return ProtocolInfo.REMOVE_OBJECTIVE_PACKET;
    }

    @Override
    public void decode() {
        //only server -> client
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.objectiveName);
    }
}
