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

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    public boolean hasAddonPacks;

    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;

//    /**
//     * @since v618
//     */
//    private List<CDNEntry> CDNEntries = new ObjectArrayList<>();


    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.mustAccept);
        byteBuf.writeBoolean(this.hasAddonPacks);
        byteBuf.writeBoolean(this.scripting);
        this.encodePacks(byteBuf, this.resourcePackEntries, false);
    }

    private void encodePacks(HandleByteBuf byteBuf, ResourcePack[] packs, boolean behaviour) {
        byteBuf.writeShortLE(packs.length);
        for (ResourcePack entry : packs) {
            byteBuf.writeString(entry.getPackId().toString());
            byteBuf.writeString(entry.getPackVersion());
            byteBuf.writeLongLE(entry.getPackSize());
            byteBuf.writeString(entry.getEncryptionKey()); // encryption key
            byteBuf.writeString(""); // sub-pack name
            byteBuf.writeString(!entry.getEncryptionKey().isEmpty() ? entry.getPackId().toString() : ""); // content identity
            byteBuf.writeBoolean(false); // scripting
            byteBuf.writeBoolean(false);    // isAddonPack
            byteBuf.writeString(entry.cdnUrl());    // cdnUrl
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

    public ResourcePack[] getResourcePackEntries() {
        return resourcePackEntries;
    }

    public void setResourcePackEntries(ResourcePack[] resourcePackEntries) {
        this.resourcePackEntries = resourcePackEntries;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
