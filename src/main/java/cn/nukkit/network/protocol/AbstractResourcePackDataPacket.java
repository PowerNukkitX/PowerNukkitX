package cn.nukkit.network.protocol;

import lombok.NoArgsConstructor;
import cn.nukkit.utils.version.Version;

import java.util.UUID;


@NoArgsConstructor(onConstructor = @__())
public abstract class AbstractResourcePackDataPacket extends DataPacket {


    public abstract Version getPackVersion();


    public abstract void setPackVersion(Version version);


    public abstract UUID getPackId();


    public abstract void setPackId(UUID uuid);


    protected void decodePackInfo() {
        String packInfo = this.getString();
        String[] packInfoParts = packInfo.split("_", 2);
        try {
            setPackId(UUID.fromString(packInfoParts[0]));
        } catch (IllegalArgumentException exception) {
            setPackId(null);
        }
        setPackVersion((packInfoParts.length > 1)? new Version(packInfoParts[1]) : null);
    }

    protected void encodePackInfo() {
        UUID packId = getPackId();
        Version packVersion = getPackVersion();
        String packInfo = (packId != null) ? packId.toString() : new UUID(0, 0).toString();
        if (packVersion != null) {
            packInfo += "_" + packVersion;
        }
        this.putString(packInfo);
    }
}
