package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.version.Version;

import java.util.UUID;


public abstract class AbstractResourcePackDataPacket extends DataPacket {
    public abstract Version getPackVersion();

    public abstract void setPackVersion(Version version);

    public abstract UUID getPackId();

    public abstract void setPackId(UUID uuid);

    
    /**
     * @deprecated 
     */
    protected void decodePackInfo(HandleByteBuf byteBuf) {
        String $1 = byteBuf.readString();
        String[] packInfoParts = packInfo.split("_", 2);
        try {
            setPackId(UUID.fromString(packInfoParts[0]));
        } catch (IllegalArgumentException exception) {
            setPackId(null);
        }
        setPackVersion((packInfoParts.length > 1)? new Version(packInfoParts[1]) : null);
    }

    
    /**
     * @deprecated 
     */
    protected void encodePackInfo(HandleByteBuf byteBuf) {
        UUID $2 = getPackId();
        Version $3 = getPackVersion();
        String $4 = (packId != null) ? packId.toString() : new UUID(0, 0).toString();
        if (packVersion != null) {
            packInfo += "_" + packVersion;
        }
        byteBuf.writeString(packInfo);
    }
}
