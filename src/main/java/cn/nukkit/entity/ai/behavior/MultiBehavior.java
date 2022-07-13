package cn.nukkit.entity.ai.behavior;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 由多个行为组成的组（注意和BehaviorGroup区分）
 * 调用方法execute()前，必须调用此对象的评估函数以确认激活的是哪个行为
 * 在评估时，会将评估下发到所有包含的行为
 * 对于返回成功的行为，将会选取最高优先级的那一组
 * 如果到这一步依然存在多个行为，则会随机选取其中一个作为执行行为
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MultiBehavior implements IBehavior {

    protected Set<IBehavior> behaviors;
    @Setter
    protected IBehavior currentBehavior;
    /**
     * 此组的优先级
     * 在BehaviorGroup中，获取优先级将会返回此值指代整个组的优先级
     */
    protected final int priority;

    private static final Random rand = new Random();

    public MultiBehavior(int priority,IBehavior ...behaviors){
        this.priority = priority;
        this.behaviors = Set.of(behaviors);
    }

    public MultiBehavior(int priority, Set<IBehavior> behaviors) {
        this.priority = priority;
        this.behaviors = behaviors;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        var result = evaluateBehaviors(entity);
        if (result.isEmpty()) {
            return false;
        }
        if (result.size() == 1) {
            setCurrentBehavior(result.iterator().next());
            return true;
        }
        //根据Weight选取一个行为
        int totalWeight = 0;
        for (IBehavior behavior : result) {
            totalWeight += behavior.getWeight();
        }
        int random = rand.nextInt(totalWeight + 1);
        for (IBehavior behavior : result){
            random -= behavior.getWeight();
            if (random <= 0) {
                setCurrentBehavior(behavior);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return false;
        }
        return currentBehavior.execute(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.onInterrupt(entity);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.onStart(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if (currentBehavior == null) {
            return;
        }
        currentBehavior.onStop(entity);
    }

    /**
     * @param entity 实体
     * @return 最高优先级且评估成功的一组行为（包含评估结果）
     */
    protected Set<IBehavior> evaluateBehaviors(EntityIntelligent entity) {
        //存储评估成功的行为（未过滤优先级）
        var evalSucceed = new HashSet<IBehavior>();
        int highestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            if (behavior.evaluate(entity)) {
                evalSucceed.add(behavior);
                if (behavior.getPriority() > highestPriority) {
                    highestPriority = behavior.getPriority();
                }
            }
        }
        //如果没有评估结果，则返回空
        if (evalSucceed.isEmpty()) {
            return evalSucceed;
        }
        //过滤掉低优先级的行为
        var result = new HashSet<IBehavior>();
        for (IBehavior entry : evalSucceed) {
            if (entry.getPriority() == highestPriority) {
                result.add(entry);
            }
        }
        return result;
    }
}
