package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 行为评估器
 * 决定是否执行与其绑定的行为执行器
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorEvaluator {

    /**
     *
     * @param entity
     * 评估目标实体
     *
     * @return boolean
     * 是否需要启动绑定的执行器
     *
     */
    boolean evaluate(EntityIntelligent entity);
}
