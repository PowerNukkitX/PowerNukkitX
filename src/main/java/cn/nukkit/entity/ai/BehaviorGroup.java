package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.message.Message;
import cn.nukkit.level.Position;
import lombok.Getter;

import java.util.*;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class BehaviorGroup implements IBehaviorGroup {

    //全部行为
    protected Set<IBehavior> behaviors = new HashSet<>();
    //正在运行的行为
    protected Set<IBehavior> runningBehaviors = new HashSet<>();

    public BehaviorGroup(IBehavior ...behaviors){
        this.behaviors.addAll(Set.of(behaviors));
    }

    public BehaviorGroup(List<IBehavior> behaviors){
        this.behaviors.addAll(behaviors);
    }

    /**
     *
     * @param entity
     * @param message
     * 向行为组发送消息，行为组将会评估所有行为
     */
    public void message(EntityIntelligent entity, Message message){
        Map<IBehavior,Position> result = evaluateBehaviors(entity, message);
        if (result.isEmpty()) return;
        //当前运行的行为的优先级（优先级必定都是一样的，所以说不需要比较得出）
        int currentHeightestPriority = runningBehaviors.isEmpty() ? Integer.MIN_VALUE : runningBehaviors.iterator().next().getPriority();
        //result的行为优先级
        int resultHeightestPriority = result.keySet().iterator().next().getPriority();
        if (resultHeightestPriority < currentHeightestPriority) return;//如果result的优先级低于当前运行的行为，则不执行
        if (resultHeightestPriority > currentHeightestPriority) {
            //如果result的优先级比当前运行的行为的优先级高，则替换当前运行的所有行为
            interruptAllRunningBehaviors(entity);
            runningBehaviors.addAll(result.keySet());
        }
        //如果result的优先级和当前运行的行为的优先级一样，则添加result的行为
        if (resultHeightestPriority == currentHeightestPriority) {
            addToRunningBehaviors(entity,result);
        }
    }

    /**
     * @Param Map<IBehavior,Position>
     * IBehavior -> 要添加的行为
     * Position -> 评估器的返回值，将会传递给行为的执行器的onStart()方法
     */
    protected void addToRunningBehaviors(EntityIntelligent entity, Map<IBehavior,Position> behaviors){
        behaviors.forEach((behavior,position)->{
            behavior.onStart(entity,position);
            runningBehaviors.add(behavior);
        });
    }

    /**
     * 运行并刷新正在运行的行为
     */
    public void tickRunningBehaviors(EntityIntelligent entity){
        Set<IBehavior> removed = new HashSet<>();
        for (IBehavior behavior : runningBehaviors){
            if (!behavior.execute(entity)){
                removed.add(behavior);
                behavior.onStop(entity);
            }
        }
        runningBehaviors.removeAll(removed);
    }

    /**
     * 中断所有正在运行的行为
     */
    protected void interruptAllRunningBehaviors(EntityIntelligent entity){
        for (IBehavior behavior : runningBehaviors){
            behavior.onInterrupt(entity);
        }
        runningBehaviors.clear();
    }

    /**
     * @param entity
     * @param message
     * @return 最高优先级且评估成功的一组行为（包含评估结果）
     */
    protected Map<IBehavior,Position> evaluateBehaviors(EntityIntelligent entity, Message message){
        //存储评估成功的行为（未过滤优先级）
        Map<IBehavior,Position> evalSucceed = new HashMap<>();
        int heightestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            //若已经在运行了，就不需要评估了
            if (runningBehaviors.contains(behavior)) continue;
            Position position = behavior.evaluate(entity, message);
            if(position != null){
                evalSucceed.put(behavior,position);
                if(behavior.getPriority() > heightestPriority){
                    heightestPriority = behavior.getPriority();
                }
            }
        }
        //如果没有评估结果，则返回空
        if(evalSucceed.isEmpty()) return evalSucceed;
        //过滤掉低优先级的行为
        Map<IBehavior,Position> result = new HashMap<>();
        for (Map.Entry<IBehavior,Position> entry : evalSucceed.entrySet()) {
            if(entry.getKey().getPriority() == heightestPriority){
                result.put(entry.getKey(),entry.getValue());
            }
        }
        return result;
    }

    protected int getHeightestPriority(Set<IBehavior> behaviors){
        int heightestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            if(behavior.getPriority() > heightestPriority){
                heightestPriority = behavior.getPriority();
            }
        }
        return heightestPriority;
    }
}
