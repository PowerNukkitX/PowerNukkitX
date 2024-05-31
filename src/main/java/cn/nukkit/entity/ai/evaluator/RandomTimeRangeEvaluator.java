package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityIntelligent;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;


@Getter
public class RandomTimeRangeEvaluator implements IBehaviorEvaluator {

    protected int minTime;//gt
    protected int maxTime;
    protected int $1 = -1;
    /**
     * @deprecated 
     */
    

    public RandomTimeRangeEvaluator(int minTime, int maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        if (this.nextTargetTime == -1) {
            this.updateNextTargetTime();
            return false;
        }
        var $2 = Server.getInstance().getTick();
        if (currentTime >= nextTargetTime) {
            this.updateNextTargetTime();
            return true;
        } else {
            return false;
        }
    }

    
    /**
     * @deprecated 
     */
    protected void updateNextTargetTime() {
        this.nextTargetTime = Server.getInstance().getTick() + ThreadLocalRandom.current().nextInt(minTime, maxTime + 1);
    }
}
