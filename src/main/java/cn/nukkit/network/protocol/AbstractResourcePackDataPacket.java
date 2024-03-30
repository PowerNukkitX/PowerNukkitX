package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.version.Version;

import java.util.UUID;


public abstract class AbstractResourcePackDataPacket extends DataPacket {
    public abstract Version getPackVersion();

    public abstract void setPackVersion(Version version);

    public abstract UUID getPackId();

    public abstract void setPackId(UUID uuid);

    protected void decodePackInfo(HandleByteBuf byteBuf) {
        String packInfo = byteBuf.readString();
        String[] packInfoParts = packInfo.split("_", 2);
        try {
            setPackId(UUID.fromString(packInfoParts[0]));
        } catch (IllegalArgumentException exception) {
            setPackId(null);
        }
        setPackVersion((packInfoParts.length > 1)? new Version(packInfoParts[1]) : null);
    }

    protected void encodePackInfo(HandleByteBuf byteBuf) {
        UUID packId = getPackId();
        Version packVersion = getPackVersion();
        String packInfo = (packId != null) ? packId.toString() : new UUID(0, 0).toString();
        if (packVersion != null) {
            packInfo += "_" + packVersion;
        }
        byteBuf.writeString(packInfo);
    }
}
