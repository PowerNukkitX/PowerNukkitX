package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;
import org.powernukkitx.math.Vector3;

/**
 * Some common entity movement control methods
 * <p>
 * Some general entity motion control methods
 */


public interface EntityControlUtils {
    default Vector3 getLookTarget() {
        return getMemoryStorage().get(CoreMemoryTypes.LOOK_TARGET);
    }

    default void setLookTarget(Vector3 lookTarget) {
        getMemoryStorage().put(CoreMemoryTypes.LOOK_TARGET, lookTarget);
    }

    default Vector3 getMoveTarget() {
        return getMemoryStorage().get(CoreMemoryTypes.MOVE_TARGET);
    }

    default void setMoveTarget(Vector3 moveTarget) {
        getMemoryStorage().put(CoreMemoryTypes.MOVE_TARGET, moveTarget);
    }

    default Vector3 getMoveDirectionStart() {
        return getMemoryStorage().get(CoreMemoryTypes.MOVE_DIRECTION_START);
    }

    default void setMoveDirectionStart(Vector3 moveDirectionStart) {
        getMemoryStorage().put(CoreMemoryTypes.MOVE_DIRECTION_START, moveDirectionStart);
    }

    default boolean hasMoveDirection() {
        return getMoveDirectionStart() != null && getMoveDirectionEnd() != null;
    }

    default Vector3 getMoveDirectionEnd() {
        return getMemoryStorage().get(CoreMemoryTypes.MOVE_DIRECTION_END);
    }

    default void setMoveDirectionEnd(Vector3 moveDirectionEnd) {
        getMemoryStorage().put(CoreMemoryTypes.MOVE_DIRECTION_END, moveDirectionEnd);
    }

    default boolean isShouldUpdateMoveDirection() {
        return getMemoryStorage().get(CoreMemoryTypes.SHOULD_UPDATE_MOVE_DIRECTION);
    }

    default void setShouldUpdateMoveDirection(boolean shouldUpdateMoveDirection) {
        getMemoryStorage().put(CoreMemoryTypes.SHOULD_UPDATE_MOVE_DIRECTION, shouldUpdateMoveDirection);
    }

    default boolean isEnablePitch() {
        return getMemoryStorage().get(CoreMemoryTypes.ENABLE_PITCH);
    }

    default void setEnablePitch(boolean enablePitch) {
        getMemoryStorage().put(CoreMemoryTypes.ENABLE_PITCH, enablePitch);
    }

//    not used for now
//
//
//    public boolean isEnableYaw() {
//        return getMemoryStorage().get(CoreMemoryTypes.ENABLE_YAW);
//    }
//
//
//
//    public void setEnableYaw(boolean enableYaw) {
//        getMemoryStorage().put(CoreMemoryTypes.ENABLE_YAW, enableYaw);
//    }
//
//
//
//    public boolean isEnableHeadYaw() {
//        return getMemoryStorage().get(CoreMemoryTypes.ENABLE_HEAD_YAW);
//    }
//
//
//
//    public void setEnableHeadYaw(boolean enableHeadYaw) {
//        getMemoryStorage().put(CoreMemoryTypes.ENABLE_HEAD_YAW, enableHeadYaw);
//    }

    IMemoryStorage getMemoryStorage();
}
