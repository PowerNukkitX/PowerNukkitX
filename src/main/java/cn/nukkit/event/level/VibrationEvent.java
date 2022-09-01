package cn.nukkit.event.level;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public abstract class VibrationEvent extends Event implements Cancellable {

    protected cn.nukkit.level.vibration.VibrationEvent vibrationEvent;

    public VibrationEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent) {
        this.vibrationEvent = vibrationEvent;
    }

    public cn.nukkit.level.vibration.VibrationEvent getVibrationEvent() {
        return vibrationEvent;
    }
}
