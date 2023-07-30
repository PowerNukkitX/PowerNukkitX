package cn.nukkit.event.level;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.vibration.VibrationListener;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public class VibrationArriveEvent extends VibrationEvent {

    protected VibrationListener listener;

    public VibrationArriveEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent, VibrationListener listener) {
        super(vibrationEvent);
        this.listener = listener;
    }
}
