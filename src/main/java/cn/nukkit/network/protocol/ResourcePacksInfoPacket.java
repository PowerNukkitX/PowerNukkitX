package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePacksInfoPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    public boolean hasAddonPacks;

    public boolean forceServerPacks;
    public ResourcePack[] behaviourPackEntries = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;
    /**
     * @since v618
     */
    private List<CDNEntry> CDNEntries = new ObjectArrayList<>();

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.mustAccept);
        byteBuf.writeBoolean(this.hasAddonPacks);
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

    
    /**
     * @deprecated 
     */
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
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    /**
     * @deprecated 
     */
    

    public boolean isForcedToAccept() {
        return mustAccept;
    }
    /**
     * @deprecated 
     */
    

    public void setForcedToAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }
    /**
     * @deprecated 
     */
    

    public boolean isScriptingEnabled() {
        return scripting;
    }
    /**
     * @deprecated 
     */
    

    public void setScriptingEnabled(boolean scripting) {
        this.scripting = scripting;
    }

    public ResourcePack[] getBehaviourPackEntries() {
        return behaviourPackEntries;
    }
    /**
     * @deprecated 
     */
    

    public void setBehaviourPackEntries(ResourcePack[] behaviourPackEntries) {
        this.behaviourPackEntries = behaviourPackEntries;
    }

    public ResourcePack[] getResourcePackEntries() {
        return resourcePackEntries;
    }
    /**
     * @deprecated 
     */
    

    public void setResourcePackEntries(ResourcePack[] resourcePackEntries) {
        this.resourcePackEntries = resourcePackEntries;
    }
    /**
     * @deprecated 
     */
    

    public boolean isForcingServerPacksEnabled() {
        return forceServerPacks;
    }
    /**
     * @deprecated 
     */
    

    public void setForcingServerPacksEnabled(boolean forcingServerPacksEnabled) {
        this.forceServerPacks = forcingServerPacksEnabled;
    }
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
