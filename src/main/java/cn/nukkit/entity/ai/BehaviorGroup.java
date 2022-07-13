package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.*;
import cn.nukkit.entity.ai.route.ConcurrentRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class BehaviorGroup implements IBehaviorGroup {

    //全部行为
    protected final Set<IBehavior> behaviors = new HashSet<>();
    //传感器
    protected final Set<ISensor> sensors = new HashSet<>();
    //控制器
    protected final Set<IController> controllers = new HashSet<>();
    //正在运行的行为
    protected final Set<IBehavior> runningBehaviors = new HashSet<>();
    //记忆存储器
    protected final MemoryStorage memory = new MemoryStorage();
    //寻路器(使用异步寻路器)
    protected ConcurrentRouteFinder routeFinder;
    protected boolean updatingRoute = false;

    public BehaviorGroup(Set<IBehavior> behaviors, Set<ISensor> sensors, Set<IController> controllers,ConcurrentRouteFinder routeFinder) {
        this.behaviors.addAll(behaviors);
        this.sensors.addAll(sensors);
        this.controllers.addAll(controllers);
        this.routeFinder = routeFinder;
    }

    public void addBehavior(IBehavior behavior){
        this.behaviors.add(behavior);
    }

    public void addSensor(ISensor sensor){
        this.sensors.add(sensor);
    }

    public void addController(IController controller){
        this.controllers.add(controller);
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

    public void collectSensorData(EntityIntelligent entity){
        for (ISensor sensor : sensors){
            IMemory<?> memory = sensor.sense(entity);
            if (memory.getData() == null)
                this.memory.remove(memory.getClass());
            else
                this.memory.put(memory);
        }
    }

    /**
     *
     * @param entity
     * 评估所有行为
     */
    public void evaluateBehaviors(EntityIntelligent entity){
        //存储评估成功的行为（未过滤优先级）
        var evalSucceed = new HashSet<IBehavior>();
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
        var result = new HashSet<IBehavior>();
        for (var entry : evalSucceed) {
            if(entry.getPriority() == heightestPriority){
                result.add(entry);
            }
        }
        if (result.isEmpty()) return;
        //当前运行的行为的优先级（优先级必定都是一样的，所以说不需要比较得出）
        int currentHighestPriority = runningBehaviors.isEmpty() ? Integer.MIN_VALUE : runningBehaviors.iterator().next().getPriority();
        //result的行为优先级
        int resultHighestPriority = result.iterator().next().getPriority();
        if (resultHighestPriority < currentHighestPriority) return;//如果result的优先级低于当前运行的行为，则不执行
        if (resultHighestPriority > currentHighestPriority) {
            //如果result的优先级比当前运行的行为的优先级高，则替换当前运行的所有行为
            interruptAllRunningBehaviors(entity);
            runningBehaviors.addAll(result);
        }
        //如果result的优先级和当前运行的行为的优先级一样，则添加result的行为
        if (resultHighestPriority == currentHighestPriority) {
            addToRunningBehaviors(entity,result);
        }
    }

    @Override
    public void applyController(EntityIntelligent entity) {
        for (IController controller : controllers){
            controller.control(entity);
        }
    }

    @Override
    public void updateRoute(EntityIntelligent entity) {
        if (needUpdateRoute() && !updatingRoute){
            //目的地已更新，需要更新路线但还没开始重新规划路线
            Vector3 target = getRouteTarget();
            //clone防止寻路器潜在的修改
            routeFinder.setStart(entity.clone());
            routeFinder.setTarget(target);
            routeFinder.asyncSearch();
            updatingRoute = true;
        }else if (needUpdateRoute() && updatingRoute){
            //已经开始重新规划路线，检查是否规划完毕
            if (routeFinder.isFinished()){
                //规划完毕，更新Memory
                updatingRoute = false;
                setTargetUpdated();
            }
        }
        if (needUpdateMoveDirection()){
            if (routeFinder.hasNext()){
                //若有新的移动方向，则更新
                updateMoveDestination(entity);
                setMoveDirectionUpdated();
            }
        }
    }

    protected boolean needUpdateRoute(){
        return memory.contains(NeedUpdateRouteMemory.class);
    }

    @Nullable
    protected Vector3 getRouteTarget(){
        return memory.contains(MoveTargetMemory.class) ? (Vector3)memory.get(MoveTargetMemory.class).getData() : null;
    }

    protected void setTargetUpdated(){
        memory.remove(NeedUpdateRouteMemory.class);
    }

    protected boolean needUpdateMoveDirection(){
        return memory.contains(NeedUpdateMoveDestinationMemory.class);
    }

    protected void updateMoveDestination(EntityIntelligent entity){
        MoveDirectionMemory directionMemory = (MoveDirectionMemory)memory.get(MoveDirectionMemory.class);
        Vector3 end = null;
        if (directionMemory != null){
            end = directionMemory.getEnd();
        }else{
            end = entity.clone();
        }
        memory.put(new MoveDirectionMemory(end,routeFinder.next().getVector3()));
    }

    protected void setMoveDirectionUpdated(){
        memory.remove(NeedUpdateMoveDestinationMemory.class);
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
     * 中断所有正在运行的行为
     */
    protected void interruptAllRunningBehaviors(EntityIntelligent entity){
        for (IBehavior behavior : runningBehaviors){
            behavior.onInterrupt(entity);
        }
        runningBehaviors.clear();
    }

    /**
     * 获取指定Set<IBehavior>内的最高优先级
     * @param behaviors 行为组
     * @return int
     * 最高优先级
     */
    protected int getHighestPriority(@NotNull Set<IBehavior> behaviors){
        int highestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            if(behavior.getPriority() > highestPriority){
                highestPriority = behavior.getPriority();
            }
        }
        return highestPriority;
    }
}
