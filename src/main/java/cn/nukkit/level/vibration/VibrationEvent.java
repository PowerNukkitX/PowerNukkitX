package cn.nukkit.level.vibration;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

/**
 * @param initiator The object which cause the vibration (can be an instance of Block, Entity ...)
 * @param source    The vibration source pos
 * @param type      Vibration type
 */
@PowerNukkitXOnly
@Since("1.19.21-r3")
public record VibrationEvent(Object initiator, Vector3 source, VibrationType type) {
    //nothing
}
