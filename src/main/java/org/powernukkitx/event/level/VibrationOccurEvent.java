package org.powernukkitx.event.level;

import org.powernukkitx.event.HandlerList;

public class VibrationOccurEvent extends VibrationEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public VibrationOccurEvent(org.powernukkitx.level.vibration.VibrationEvent vibrationEvent) {
        super(vibrationEvent);
    }
}
