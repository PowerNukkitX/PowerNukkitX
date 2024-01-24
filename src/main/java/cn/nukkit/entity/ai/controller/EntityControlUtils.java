package cn.nukkit.entity.ai.controller;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.math.Vector3;

/**
 * 一些通用的实体运动控制方法
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

//    暂时不使用
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
