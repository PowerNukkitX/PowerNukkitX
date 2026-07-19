package org.powernukkitx.event.level;

import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.vibration.VibrationListener;


public class VibrationArriveEvent extends VibrationEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected VibrationListener listener;

    public VibrationArriveEvent(org.powernukkitx.level.vibration.VibrationEvent vibrationEvent, VibrationListener listener) {
        super(vibrationEvent);
        this.listener = listener;
    }
}
