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
    public ResourcePack[] behaviourPackEntries = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;
    private boolean forcingServerPacksEnabled;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.scripting);
        this.putBoolean(this.forcingServerPacksEnabled);

        encodePacks(this.behaviourPackEntries);
        encodePacks(this.resourcePackEntries);
    }

    private void encodePacks(ResourcePack[] packs) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(""); // encryption key
            this.putString(""); // sub-pack name
            this.putString(entry.getPackId().toString()); // content identity
            this.putBoolean(false); // scripting
            this.putBoolean(false); // raytracing capable
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
        return forcingServerPacksEnabled;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setForcingServerPacksEnabled(boolean forcingServerPacksEnabled) {
        this.forcingServerPacksEnabled = forcingServerPacksEnabled;
    }
}
