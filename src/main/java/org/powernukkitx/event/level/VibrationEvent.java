package org.powernukkitx.event.level;

import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;


public abstract class VibrationEvent extends Event implements Cancellable {

    protected org.powernukkitx.level.vibration.VibrationEvent vibrationEvent;

    public VibrationEvent(org.powernukkitx.level.vibration.VibrationEvent vibrationEvent) {
        this.vibrationEvent = vibrationEvent;
    }

    public org.powernukkitx.level.vibration.VibrationEvent getVibrationEvent() {
        return vibrationEvent;
    }
}
