package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.EntityIntelligent;

/**
 * Sensor是具有ai的实体获取额外信息的传感器，Sensor应该将外部信息放入实体的Memory中以供Goal检索。
 *
 * @see cn.nukkit.entity.EntityIntelligent
 */
public interface Sensor {
    /**
     * 是否应该在当前游戏刻获取传感信息，常用于判断延迟优化等
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     * @return 是否应该获取传感信息，如果为真，那么{@link Sensor#sense(int, EntityIntelligent)}将会随即被调用
     */
    default boolean shouldSense(int currentTick, EntityIntelligent entity) {
        return entity.ifNeedsRecalcMovement();
    }

    /**
     * 获取传感信息并将信息放入实体的Memory中
     *
     * @param currentTick 当前游戏刻
     * @param entity      实体
     */
    void sense(int currentTick, EntityIntelligent entity);
}
