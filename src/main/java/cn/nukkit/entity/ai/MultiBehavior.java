package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityAI;
import cn.nukkit.level.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
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

    public MultiBehavior(int priority,IBehavior ...behaviors){
        this.priority = priority;
        this.behaviors = Set.of(behaviors);
    }

    public MultiBehavior(int priority,Set<IBehavior> behaviors){
        this.priority = priority;
        this.behaviors = behaviors;
    }

    @Override
    public int getPriority() {
        return priority;
    }
    @Override
    public Position evaluate(EntityAI entity, Message message) {
        Map<IBehavior,Position> result = evaluateBehaviors(entity, message);
        if (result.isEmpty()){
            return null;
        }
        if (result.size() == 1){
            setCurrentBehavior(result.keySet().iterator().next());
            return result.values().iterator().next();
        }
        //随机选取一个
        //todo: 也许我们可以加入权重系统？
        Set<Map.Entry<IBehavior, Position>> entries = result.entrySet();
        int index = (int) (Math.random() * entries.size());
        Map.Entry<IBehavior, Position> entry = entries.stream().skip(index).findFirst().get();
        setCurrentBehavior(entry.getKey());
        return entry.getValue();
    }

    @Override
    public boolean execute(EntityAI entity) {
        if (currentBehavior == null){
            return false;
        }
        return currentBehavior.execute(entity);
    }

    @Override
    public void onInterrupt(EntityAI entity) {
        if (currentBehavior == null){
            return;
        }
        currentBehavior.onInterrupt(entity);
    }

    @Override
    public void onStart(EntityAI entity, Position target) {
        if (currentBehavior == null){
            return;
        }
        currentBehavior.onStart(entity,target);
    }

    @Override
    public void onStop(EntityAI entity) {
        if (currentBehavior == null){
            return;
        }
        currentBehavior.onStop(entity);
    }

    /**
     *
     * @param entity
     * @param message
     * @return 最高优先级且评估成功的一组行为（包含评估结果）
     */
    protected Map<IBehavior,Position> evaluateBehaviors(EntityAI entity, Message message){
        //存储评估成功的行为（未过滤优先级）
        Map<IBehavior,Position> evalSucceed = new HashMap<>();
        int heightestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            Position position = behavior.evaluate(entity, message);
            if(position != null){
                evalSucceed.put(behavior,position);
                if(behavior.getPriority() > heightestPriority){
                    heightestPriority = behavior.getPriority();
                }
            }
        }
        //如果没有评估结果，则返回空
        if(evalSucceed.isEmpty()){
            return evalSucceed;
        }
        Map<IBehavior,Position> result = new HashMap<>();
        //过滤掉低优先级的行为
        for (Map.Entry<IBehavior,Position> entry : evalSucceed.entrySet()) {
            if(entry.getKey().getPriority() == heightestPriority){
                result.put(entry.getKey(),entry.getValue());
            }
        }
        return result;
    }
}
