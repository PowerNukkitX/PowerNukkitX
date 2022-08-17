package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackStack = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackStack = ResourcePack.EMPTY_ARRAY;
    public final List<ExperimentData> experiments = new ObjectArrayList<>();
    public String gameVersion = "*";

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);

        this.putUnsignedVarInt(this.behaviourPackStack.length);
        for (ResourcePack entry : this.behaviourPackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString(""); //TODO: subpack name
        }

        this.putUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack entry : this.resourcePackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString(""); //TODO: subpack name
        }

        this.putString(this.gameVersion);
        this.putLInt(this.experiments.size()); // Experiments length
        for (ExperimentData experimentData : this.experiments) {
            this.putString(experimentData.getName());
            this.putBoolean(experimentData.isEnabled());
        }
        this.putBoolean(true); // Were experiments previously toggled
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Value
    public static class ExperimentData {
        String name;
        boolean enabled;
    }
}
