package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.EduSharedUriResource;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class EduUriResourcePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.EDU_URI_RESOURCE_PACKET;
    public EduSharedUriResource eduSharedUriResource;


    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        String buttonName = this.getString();
        String linkUri = this.getString();
        this.eduSharedUriResource = new EduSharedUriResource(buttonName, linkUri);
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(eduSharedUriResource.buttonName());
        this.putString(eduSharedUriResource.linkUri());
    }
}
