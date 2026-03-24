package cn.nukkit.network.protocol.types.clock;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

@Data
public class SyncStateData implements ClockPayloadData {

    private final List<SyncWorldClockStateData> clockData = new ObjectArrayList<>();

    @Override
    public Type getType() {
        return Type.SYNC_STATE_DATA;
    }
}