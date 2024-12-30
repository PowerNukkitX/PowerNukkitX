package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

import java.util.Set;

public class RandomSoundEvaluator extends AllMatchEvaluator {

    public RandomSoundEvaluator() {
        this(10, 10);
    }

    public RandomSoundEvaluator(int ticks, int probability) {
        super(Set.of(
            entity -> entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET),
            entity -> entity.getLevel().getTick()%ticks == 0,
            new ProbabilityEvaluator(1, probability)
        ));
    }

}
