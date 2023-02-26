package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 此接口抽象了一个行为评估器 <br/> 决定是否激活与其绑定的执行器
 * <p>
 * This interface abstracts a Behavior Evaluator that <br> decides whether to activate the Actuator bound to it
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehaviorEvaluator {

    /**
     * 是否需要激活与其绑定的执行器
     * <p>
     * Whether the executor bound to it needs to be activated
     *
     * @param entity 评估目标实体<br>Assess the targetEntity
     * @return 是否需要激活<br>Do you need to activate
     */
    boolean evaluate(EntityIntelligent entity);
}
