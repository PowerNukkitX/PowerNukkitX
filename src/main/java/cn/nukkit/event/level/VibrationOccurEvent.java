package cn.nukkit.event.level;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public class VibrationOccurEvent extends VibrationEvent {

    public VibrationOccurEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent) {
        super(vibrationEvent);
    }
}
