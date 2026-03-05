package cn.nukkit.network.protocol.types.clock;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

@Data
public class AddTimeMarkerData implements ClockPayloadData {

    private long clockId;
    private final List<TimeMarkerData> timeMarkers = new ObjectArrayList<>();

    @Override
    public ClockPayloadData.Type getType() {
        return Type.ADD_TIME_MARKER_DATA;
    }
}