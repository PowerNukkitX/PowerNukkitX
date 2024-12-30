package cn.nukkit.entity.ai.evaluator;

import java.util.Set;

public class RandomSoundEvaluator extends AllMatchEvaluator {

    public RandomSoundEvaluator() {
        this(20, 10);
    }

    public RandomSoundEvaluator(int ticks, int probability) {
        super(Set.of(
            entity -> entity.getLevel().getTick()%ticks == 0,
            new ProbabilityEvaluator(1, probability)
        ));
    }

}
