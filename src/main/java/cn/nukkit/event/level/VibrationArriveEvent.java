package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.vibration.VibrationListener;


public class VibrationArriveEvent extends VibrationEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected VibrationListener listener;

    public VibrationArriveEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent, VibrationListener listener) {
        super(vibrationEvent);
        this.listener = listener;
    }
}
