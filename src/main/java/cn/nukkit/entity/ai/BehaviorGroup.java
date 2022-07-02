package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.MemoryStorage;
import cn.nukkit.entity.ai.sensor.ISensor;
import lombok.Getter;

import java.util.*;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class BehaviorGroup implements IBehaviorGroup {

    //全部行为
    protected Set<IBehavior> behaviors = new HashSet<>();
    //传感器
    protected Set<ISensor> sensors = new HashSet<>();
    //记忆存储器
    protected MemoryStorage memory = new MemoryStorage();
    //正在运行的行为
    protected Set<IBehavior> runningBehaviors = new HashSet<>();

    public BehaviorGroup(Set<IBehavior> behaviors, Set<ISensor> sensors){
        this.behaviors.addAll(behaviors);
        this.sensors.addAll(sensors);
    }

    public void tick(EntityIntelligent entity){
        //搜集信息并写入记忆
        collectSensorData(entity);
        //刷新正在运行的行为
        tickRunningBehaviors(entity);
        //评估所有行为
        evaluateBehaviors(entity);
    }

    public void addBehavior(IBehavior behavior){
        this.behaviors.add(behavior);
    }

    public void addSensor(ISensor sensor){
        this.sensors.add(sensor);
    }

    /**
     * @Param Map<IBehavior,Position>
     * IBehavior -> 要添加的行为
     * Position -> 评估器的返回值，将会传递给行为的执行器的onStart()方法
     */
    protected void addToRunningBehaviors(EntityIntelligent entity, Set<IBehavior> behaviors){
        behaviors.forEach((behavior)->{
            behavior.onStart(entity);
            runningBehaviors.add(behavior);
        });
    }

    /**
     * 运行并刷新正在运行的行为
     */
    protected void tickRunningBehaviors(EntityIntelligent entity){
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

    protected void collectSensorData(EntityIntelligent entity){
        for (ISensor sensor : sensors){
            IMemory<?> memory = sensor.sense(entity);
            this.memory.put(memory);
        }
    }

    /**
     * @param entity
     * @return 评估所有行为
     */
    protected void evaluateBehaviors(EntityIntelligent entity){
        //存储评估成功的行为（未过滤优先级）
        Set<IBehavior> evalSucceed = new HashSet<>();
        int heightestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            //若已经在运行了，就不需要评估了
            if (runningBehaviors.contains(behavior)) continue;
            if(behavior.evaluate(entity)){
                evalSucceed.add(behavior);
                if(behavior.getPriority() > heightestPriority){
                    heightestPriority = behavior.getPriority();
                }
            }
        }
        //如果没有评估结果，则返回空
        if(evalSucceed.isEmpty()) return;
        //过滤掉低优先级的行为
        Set<IBehavior> result = new HashSet<>();
        for (IBehavior entry : evalSucceed) {
            if(entry.getPriority() == heightestPriority){
                result.add(entry);
            }
        }
        if (result.isEmpty()) return;
        //当前运行的行为的优先级（优先级必定都是一样的，所以说不需要比较得出）
        int currentHeightestPriority = runningBehaviors.isEmpty() ? Integer.MIN_VALUE : runningBehaviors.iterator().next().getPriority();
        //result的行为优先级
        int resultHeightestPriority = result.iterator().next().getPriority();
        if (resultHeightestPriority < currentHeightestPriority) return;//如果result的优先级低于当前运行的行为，则不执行
        if (resultHeightestPriority > currentHeightestPriority) {
            //如果result的优先级比当前运行的行为的优先级高，则替换当前运行的所有行为
            interruptAllRunningBehaviors(entity);
            runningBehaviors.addAll(result);
        }
        //如果result的优先级和当前运行的行为的优先级一样，则添加result的行为
        if (resultHeightestPriority == currentHeightestPriority) {
            addToRunningBehaviors(entity,result);
        }
    }

    /**
     * 获取指定Set<IBehavior>内的最高优先级
     * @param behaviors
     * @return int
     * 最高优先级
     */
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
