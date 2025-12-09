package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePackStackPacket extends DataPacket {
    public boolean mustAccept = false;
    public ResourcePack[] resourcePackStack = ResourcePack.EMPTY_ARRAY;
    public final List<ExperimentData> experiments = new ObjectArrayList<>();
    public String gameVersion = "*";
    public boolean isHasEditorPacks = false;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.mustAccept);


        byteBuf.writeUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack entry : this.resourcePackStack) {
            byteBuf.writeString(entry.getPackId().toString());
            byteBuf.writeString(entry.getPackVersion());
            byteBuf.writeString(""); //TODO: subpack name
        }

        byteBuf.writeString(this.gameVersion);
        byteBuf.writeIntLE(this.experiments.size()); // Experiments length
        for (ExperimentData experimentData : this.experiments) {
            byteBuf.writeString(experimentData.getName());
            byteBuf.writeBoolean(experimentData.isEnabled());
        }
        byteBuf.writeBoolean(true); // Were experiments previously toggled
        byteBuf.writeBoolean(isHasEditorPacks);
    }

    @Value
    public static class ExperimentData {
        String name;
        boolean enabled;
    }

    @Override
    public int pid() {
        return ProtocolInfo.RESOURCE_PACK_STACK_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
