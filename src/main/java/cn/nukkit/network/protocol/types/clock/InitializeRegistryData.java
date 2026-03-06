package cn.nukkit.network.protocol.types.clock;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

@Data
public class InitializeRegistryData implements ClockPayloadData {

    private final List<WorldClockData> clockData = new ObjectArrayList<>();

    @Override
    public Type getType() {
        return Type.INITIALIZE_REGISTRY_DATA;
    }
}