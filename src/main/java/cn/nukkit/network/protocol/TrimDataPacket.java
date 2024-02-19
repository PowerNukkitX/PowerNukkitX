package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.TrimMaterial;
import cn.nukkit.network.protocol.types.TrimPattern;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
public class TrimDataPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.TRIM_DATA;
    private final List<TrimPattern> patterns = new ObjectArrayList<>();
    private final List<TrimMaterial> materials = new ObjectArrayList<>();

    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        int length1 = (int) getUnsignedVarInt();
        for (int i = 0; i < length1; i++) {
            patterns.add(new TrimPattern(getString(), getString()));
        }
        int length2 = (int) getUnsignedVarInt();
        for (int i = 0; i < length2; i++) {
            materials.add(new TrimMaterial(getString(), getString(), getString()));
        }
    }

    @Override
    public void encode() {
        this.reset();
        putUnsignedVarInt(patterns.size());
        patterns.forEach(p -> {
            putString(p.itemName());
            putString(p.patternId());
        });
        putUnsignedVarInt(materials.size());
        materials.forEach(m -> {
            putString(m.materialId());
            putString(m.color());
            putString(m.itemName());
        });
    }
}
