package cn.nukkit.network.protocol.types.clock;

import lombok.Data;

@Data
public class SyncWorldClockStateData {

    private long clockId;
    private int time;
    private boolean isPaused;
}
