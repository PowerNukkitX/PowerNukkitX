package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;

public class VibrationOccurEvent extends VibrationEvent{

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public VibrationOccurEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent) {
        super(vibrationEvent);
    }
}
