package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.19.21-r4")
@Getter
public class RandomTimeRangeEvaluator implements IBehaviorEvaluator {

    protected int minTime;//gt
    protected int maxTime;
    protected int nextTargetTime = -1;

    public RandomTimeRangeEvaluator(int minTime, int maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (this.nextTargetTime == -1) {
            this.updateNextTargetTime();
            return false;
        }
        var currentTime = Server.getInstance().getTick();
        if (currentTime >= nextTargetTime) {
            this.updateNextTargetTime();
            return true;
        } else {
            return false;
        }
    }

    protected void updateNextTargetTime() {
        this.nextTargetTime = Server.getInstance().getTick() + ThreadLocalRandom.current().nextInt(minTime, maxTime + 1);
    }
}
