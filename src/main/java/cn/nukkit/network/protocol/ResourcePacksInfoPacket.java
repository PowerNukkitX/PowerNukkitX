package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
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
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.mustAccept);
        byteBuf.writeBoolean(this.scripting);
        byteBuf.writeBoolean(this.forceServerPacks);
        this.encodePacks(byteBuf, this.behaviourPackEntries, true);
        this.encodePacks(byteBuf, this.resourcePackEntries, false);
        byteBuf.writeUnsignedVarInt(getCDNEntries().size());
        for (var cdn : getCDNEntries()) {
            byteBuf.writeString(cdn.packId);
            byteBuf.writeString(cdn.remoteUrl);
        }
    }

    private void encodePacks(HandleByteBuf byteBuf, ResourcePack[] packs, boolean behaviour) {
        byteBuf.writeShortLE(packs.length);
        for (ResourcePack entry : packs) {
            byteBuf.writeString(entry.getPackId().toString());
            byteBuf.writeString(entry.getPackVersion());
            byteBuf.writeLongLE(entry.getPackSize());
            byteBuf.writeString(entry.getEncryptionKey()); // encryption key
            byteBuf.writeString(""); // sub-pack name
            byteBuf.writeString(!entry.getEncryptionKey().equals("") ? entry.getPackId().toString() : ""); // content identity
            byteBuf.writeBoolean(false); // scripting
            if (!behaviour) {
                byteBuf.writeBoolean(false); // raytracing capable
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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
