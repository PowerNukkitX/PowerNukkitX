package cn.nukkit.level.vibration;

import cn.nukkit.math.Vector3;

/**
 * @param initiator The object which cause the vibration (can be an instance of Block, Entity ...)
 * @param source    The vibration source pos
 * @param type      Vibration type
 */
public record VibrationEvent(Object initiator, Vector3 source, VibrationType type) {
    //nothing
}
