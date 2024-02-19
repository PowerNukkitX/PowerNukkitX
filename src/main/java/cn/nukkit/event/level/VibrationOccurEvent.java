package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;

public class VibrationOccurEvent extends VibrationEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public VibrationOccurEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent) {
        super(vibrationEvent);
    }
}
