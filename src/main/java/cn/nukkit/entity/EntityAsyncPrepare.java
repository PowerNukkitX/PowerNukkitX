package cn.nukkit.entity;

/**
 * 该方法将被异步并行调用，用于实体进行tick无关的操作
 */


public interface EntityAsyncPrepare {
    /**
     * 该方法将被并行执行，每一刻都执行一次，并保证每一次onUpdate之前都执行完毕
     *
     * @param currentTick 当前游戏刻
     */
    void asyncPrepare(int currentTick);
}
