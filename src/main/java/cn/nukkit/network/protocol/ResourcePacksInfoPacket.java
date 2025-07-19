package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePacksInfoPacket extends DataPacket {

    public boolean mustAccept;
    public boolean hasAddonPacks;
    public boolean scripting;

    /**
     * @since v818
     */
    public boolean disableVibrantVisuals;

    /**
     * @since v766
     */
    public UUID worldTemplateId;
    /**
     * @since v766
     */
    public String worldTemplateVersion;


    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.mustAccept);
        byteBuf.writeBoolean(this.hasAddonPacks);
        byteBuf.writeBoolean(this.scripting);
        byteBuf.writeBoolean(this.disableVibrantVisuals);
        byteBuf.writeUUID(this.worldTemplateId);
        byteBuf.writeString(this.worldTemplateVersion);
        this.encodePacks(byteBuf, this.resourcePackEntries);
    }

    private void encodePacks(HandleByteBuf byteBuf, ResourcePack[] packs) {
        byteBuf.writeShortLE(packs.length);

        for (ResourcePack entry : packs) {
            byteBuf.writeUUID(entry.getPackId());
            byteBuf.writeString(entry.getPackVersion());
            byteBuf.writeLongLE(entry.getPackSize());
            byteBuf.writeString(entry.getEncryptionKey()); // encryption key
            byteBuf.writeString(entry.getSubPackName()); // sub-pack name
            byteBuf.writeString(!entry.getEncryptionKey().isEmpty() ? entry.getPackId().toString() : ""); // content identity
            byteBuf.writeBoolean(entry.usesScript()); // scripting
            byteBuf.writeBoolean(entry.isAddonPack());    // isAddonPack
            byteBuf.writeBoolean(entry.isRaytracingCapable()); // raytracing capable
            byteBuf.writeString(entry.cdnUrl());    // cdnUrl
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
