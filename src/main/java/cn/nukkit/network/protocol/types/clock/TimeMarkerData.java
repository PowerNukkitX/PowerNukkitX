package cn.nukkit.network.protocol.types.clock;

import lombok.Data;

@Data
public class TimeMarkerData {

    private long id;
    private String name;
    private int time;
    private Integer period;
}