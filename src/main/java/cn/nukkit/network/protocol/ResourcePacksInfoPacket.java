package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    @Since("FUTURE") public boolean forceServerPacks;
    public ResourcePack[] behaviourPackEntries = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.scripting);
        this.putBoolean(this.forceServerPacks);
        this.encodePacks(this.behaviourPackEntries, true);
        this.encodePacks(this.resourcePackEntries, false);
    }

    private void encodePacks(ResourcePack[] packs, boolean behaviour) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(entry.getEncryptionKey()); // encryption key
            this.putString(""); // sub-pack name
            this.putString(!entry.getEncryptionKey().equals("") ? entry.getPackId().toString() : ""); // content identity
            this.putBoolean(false); // scripting
            if (!behaviour) {
                this.putBoolean(false); // raytracing capable
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public boolean isForcedToAccept() {
        return mustAccept;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setForcedToAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public boolean isScriptingEnabled() {
        return scripting;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setScriptingEnabled(boolean scripting) {
        this.scripting = scripting;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public ResourcePack[] getBehaviourPackEntries() {
        return behaviourPackEntries;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setBehaviourPackEntries(ResourcePack[] behaviourPackEntries) {
        this.behaviourPackEntries = behaviourPackEntries;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public ResourcePack[] getResourcePackEntries() {
        return resourcePackEntries;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setResourcePackEntries(ResourcePack[] resourcePackEntries) {
        this.resourcePackEntries = resourcePackEntries;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public boolean isForcingServerPacksEnabled() {
        return forceServerPacks;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setForcingServerPacksEnabled(boolean forcingServerPacksEnabled) {
        this.forceServerPacks = forcingServerPacksEnabled;
    }
}
