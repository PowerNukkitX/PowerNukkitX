package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityAI;
import cn.nukkit.entity.ai.message.Message;
import cn.nukkit.level.Position;

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
     * @param message
     * 此次评估相关消息
     * 例如如果评估发起源是Entity::entityBaseTick()，那么message就是EntityBaseTickMessage
     *
     * @return positon
     * 若不等于null，则说明此行为响应此次评估，调度器将会启用此行为的执行器
     * 若等于null，则说明此行为不响应此次评估
     * 返回值将会在稍后传递给执行器的onStart()方法，此返回值指代评估器感兴趣的一个对象位置
     * 例如可以返回一个实体，一个方块等，他们都属于Position类型
     *
     */
    Position evaluate(EntityAI entity, Message message);
}
