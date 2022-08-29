package cn.nukkit.level.vibration;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Position;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public record VibrationEvent(Position source, VibrationType type) {
    //nothing
}
