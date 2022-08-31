package cn.nukkit.level.vibration;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public record VibrationEvent(Vector3 source, VibrationType type) {
    //nothing
}
