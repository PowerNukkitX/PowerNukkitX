package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;

    public boolean forceServerPacks;
    public ResourcePack[] behaviourPackEntries = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;
    /**
     * @since v618
     */
    private List<CDNEntry> CDNEntries = new ObjectArrayList<>();

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
        putUnsignedVarInt(getCDNEntries().size());
        for (var cdn : getCDNEntries()) {
            putString(cdn.packId);
            putString(cdn.remoteUrl);
        }
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
    public int pid() {
        return NETWORK_ID;
    }

    public boolean isForcedToAccept() {
        return mustAccept;
    }

    public void setForcedToAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    public boolean isScriptingEnabled() {
        return scripting;
    }

    public void setScriptingEnabled(boolean scripting) {
        this.scripting = scripting;
    }

    public ResourcePack[] getBehaviourPackEntries() {
        return behaviourPackEntries;
    }

    public void setBehaviourPackEntries(ResourcePack[] behaviourPackEntries) {
        this.behaviourPackEntries = behaviourPackEntries;
    }

    public ResourcePack[] getResourcePackEntries() {
        return resourcePackEntries;
    }

    public void setResourcePackEntries(ResourcePack[] resourcePackEntries) {
        this.resourcePackEntries = resourcePackEntries;
    }

    public boolean isForcingServerPacksEnabled() {
        return forceServerPacks;
    }

    public void setForcingServerPacksEnabled(boolean forcingServerPacksEnabled) {
        this.forceServerPacks = forcingServerPacksEnabled;
    }

    public void setCDNEntries(List<CDNEntry> CDNEntries) {
        this.CDNEntries = CDNEntries;
    }

    public List<CDNEntry> getCDNEntries() {
        return CDNEntries;
    }

    @Value
    public static class CDNEntry {
        private final String packId;
        private final String remoteUrl;
    }
}
