package cn.nukkit.network.protocol.types.clock;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;

import java.util.List;

@Data
public class WorldClockData {

    private long id;
    private String name;
    private int time;
    private boolean isPaused;
    private final List<TimeMarkerData> timeMarkers = new ObjectArrayList<>();
}