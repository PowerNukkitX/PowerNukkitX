package cn.nukkit.entity.ai.evaluator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RandomSoundEvaluator extends AllMatchEvaluator {

    public RandomSoundEvaluator() {
        this(20, 10);
    }

    public RandomSoundEvaluator(int ticks, int probability) {
        super(ObjectArrayList.of(
            entity -> entity.getLevel().getTick()%ticks == 0,
            new ProbabilityEvaluator(1, probability)
        ));
    }

}
