package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;

/**
 * 此接口抽象了一个行为评估器 <br/> 决定是否激活与其绑定的执行器
 * <p>
 * This interface abstracts a Behavior Evaluator that <br> decides whether to activate the Actuator bound to it
 */


public interface IBehaviorEvaluator {

    /**
     * 是否需要激活与其绑定的执行器
     * <p>
     * 这个方法对一个行为只会评估一次，评估通过则开始运行执行器执行行为，直到行为中断或者完成，下一次评估才会开始
     * <p>
     * Whether the executor bound to it needs to be activated
     * <p>
     * This method evaluates a behavior only once, and if the evaluation passes, the executor execution behavior will start running until the behavior is interrupted or completed, and the next evaluation will not begin
     *
     * @param entity 评估目标实体<br>Assess the targetEntity
     * @return 是否需要激活<br>Do you need to activate
     */
    boolean evaluate(EntityIntelligent entity);
}
