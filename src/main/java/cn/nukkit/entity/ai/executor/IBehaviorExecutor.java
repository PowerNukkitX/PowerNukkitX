package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 行为执行器
 * 在实体上执行具体的行为
 * 对于每个实例化的实体，此对象应只会实例化一次，且一直伴随实体不会改变
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorExecutor {

    /**
     * @param entity 执行目标实体
     * @return boolean
     * 调度器将会持续执行此执行器，直到返回false，或者执行器被中断
     * <p>
     * 此方法每gt都会调用
     */
    boolean execute(EntityIntelligent entity);

    /**
     * @param entity 行为中断时(例如被更高级行为覆盖)调用
     */
    default void onInterrupt(EntityIntelligent entity) {
    }

    /**
     * @param entity 行为评估成功后调用
     */
    default void onStart(EntityIntelligent entity) {
    }

    /**
     * @param entity 行为结束时(execute()方法返回false)调用
     */
    default void onStop(EntityIntelligent entity) {
    }


    /**
     * @return String
     * 返回此执行器的名称,默认返回类名称
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    default boolean equals(IBehaviorExecutor executor) {
        return getName().equals(executor.getName());
    }
}
