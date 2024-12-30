package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@AllArgsConstructor
public class NotMatchEvaluator implements IBehaviorEvaluator {

    private IBehaviorEvaluator evaluator;

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return !evaluator.evaluate(entity);
    }
}
