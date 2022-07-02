package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.message.Message;
import cn.nukkit.level.Position;
import lombok.Getter;

/**
 * 单个的行为对象
 * 包含一个执行器和一个评估器，行为对象委托了它们的方法
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class Behavior implements IBehavior{

    protected IBehaviorExecutor executor;
    protected IBehaviorEvaluator evaluator;
    protected final int priority;

    public Behavior(IBehaviorExecutor executor, IBehaviorEvaluator evaluator, int priority){
        this.executor = executor;
        this.evaluator = evaluator;
        this.priority = priority;
    }

    @Override
    public Position evaluate(EntityIntelligent entity, Message message) {
        return evaluator.evaluate(entity, message);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return executor.execute(entity);
    }

    @Override
    public void onStart(EntityIntelligent entity, Position target) {
        executor.onStart(entity,target);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        executor.onInterrupt(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        executor.onStop(entity);
    }
}
