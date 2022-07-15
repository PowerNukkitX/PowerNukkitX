package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 行为执行器<br>
 * 在实体上执行具体的行为<br>
 * 对于每个实例化的实体，此对象应只会实例化一次，且一直伴随实体不会改变
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorExecutor {

    /**
     * 调度器将会持续执行此执行器，直到返回false，或者执行器被中断<br>
     * 此方法每gt都会调用
     *
     * @param entity 执行目标实体
     * @return boolean
     */
    boolean execute(EntityIntelligent entity);

    /**
     * 行为非正常中断时(例如被更高级行为覆盖)调用
     * @param entity 目标实体
     */
    default void onInterrupt(EntityIntelligent entity) {
    }

    /**
     * 行为评估成功后，进入激活状态前调用
     * @param entity 目标实体
     */
    default void onStart(EntityIntelligent entity) {
    }

    /**
     * 行为正常结束时(execute()方法返回false)调用
     * @param entity 目标实体
     */
    default void onStop(EntityIntelligent entity) {
    }
}
