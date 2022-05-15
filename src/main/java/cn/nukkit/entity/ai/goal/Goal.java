package cn.nukkit.entity.ai.goal;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

/**
 * 目标是一个具有AI的生物的行为操作器，它不直接操作底层的行为，如移动，跳跃，攻击等，它负责调配这些动作的组合、触发寻路或停止某些其他的行为
 */
public interface Goal extends Comparable<Goal> {
    /**
     * @return 此目标当前的执行状态
     */
    GoalState getState();

    /**
     * 如果一个目标处于非活动状态，ai将尝试启动这个目标
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     * @return 如果返回true，此目标将被启动，execute随即将被调用
     */
    default boolean shouldStart(int currentTick, EntityIntelligent entity) {
        return getState() != GoalState.WORKING && entity.ifNeedsRecalcMovement();
    }

    /**
     * 如果一个目标处于活动状态，ai将在每次执行execute前询问此tick是否应该执行此目标
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     * @return 如果返回true，ai将在此游戏刻执行这个目标
     */
    default boolean shouldContinue(int currentTick, EntityIntelligent entity) {
        return getState() == GoalState.WORKING && entity.ifNeedsRecalcMovement();
    }

    /**
     * 如果一个目标处于活动状态，ai将在每次执行execute后将会询问此tick执行后下次还是否需要执行
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     * @return 如果返回true，此目标将被关闭，不再执行
     */
    boolean shouldStop(int currentTick, EntityIntelligent entity);

    /**
     * 启动此目标，不必执行实际内容，仅初始化即可
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     */
    default void start(int currentTick, EntityIntelligent entity) {

    }

    /**
     * 执行此目标
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     */
    void execute(int currentTick, EntityIntelligent entity);

    /**
     * 结束此目标，通常用于做一些扫尾或清除状态的工作
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     */
    default void stop(int currentTick, EntityIntelligent entity) {

    }

    /**
     * @return 此目标的执行顺序，返回值越大越晚执行
     */
    int getOrder();

    @Override
    default int compareTo(@NotNull Goal o) {
        if (o.getOrder() == this.getOrder()) {
            return Integer.compare(this.hashCode(), o.hashCode());
        } else {
            return Integer.compare(this.getOrder(), o.getOrder());
        }
    }
}
