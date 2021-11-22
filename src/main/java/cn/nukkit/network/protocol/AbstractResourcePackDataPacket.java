package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.powernukkit.version.Version;

import java.util.UUID;

@PowerNukkitOnly
@Since("FUTURE")
public abstract class AbstractResourcePackDataPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("FUTURE")
    public abstract Version getPackVersion();

    @PowerNukkitOnly
    @Since("FUTURE")
    public abstract void setPackVersion(Version version);

    @PowerNukkitOnly
    @Since("FUTURE")
    public abstract UUID getPackId();

    @PowerNukkitOnly
    @Since("FUTURE")
    public abstract void setPackId(UUID uuid);

    @PowerNukkitOnly
    @Since("FUTURE")
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

    @PowerNukkitOnly
    @Since("FUTURE")
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
