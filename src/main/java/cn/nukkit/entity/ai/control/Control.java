package cn.nukkit.entity.ai.control;

import org.jetbrains.annotations.NotNull;

/**
 * 此类代表一个实体动作的控制器
 */
public interface Control<T> {
    T control(int currentTick, boolean needsRecalcMovement);
    @NotNull
    ControlState getState();
}
