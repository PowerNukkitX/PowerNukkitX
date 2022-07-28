package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.InLoveMemory;
import cn.nukkit.entity.ai.memory.SpouseMemory;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.passive.EntitySheep;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class EntityBreedingExecutor <T extends EntityAnimal> implements IBehaviorExecutor{

    protected Class<T> entityClass;
    protected int findingRangeSquared;
    protected int duration;
    protected int currentTick = 0;
    protected float moveSpeed;
    protected boolean finded;
    protected T another;

    public EntityBreedingExecutor(Class<T> entityClass, int findingRangeSquared, int duration, float moveSpeed) {
        this.entityClass = entityClass;
        this.findingRangeSquared = findingRangeSquared;
        this.duration = duration;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean execute(EntityIntelligent uncasted) {
        if (entityClass.isInstance(uncasted)) {
            T entity = entityClass.cast(uncasted);
            if (shouldFindingSpouse(entity)) {
                if (!entity.getMemoryStorage().get(InLoveMemory.class).isInLove())
                    return false;
                another = getNearestInLove(entity);
                if (another == null) return true;
                setSpouse(entity, another);

                //set move speed
                entity.setMovementSpeed(moveSpeed);
                another.setMovementSpeed(moveSpeed);

                finded = true;
            }
            if (finded) {
                currentTick++;

                updateMove(entity, another);

                if (currentTick > duration) {
                    bear(entity, another);
                    clearData(entity);
                    clearData(another);

                    currentTick = 0;
                    finded = false;
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        currentTick = 0;
        finded = false;
    }

    protected void setSpouse(T entity1,T entity2){
        entity1.getMemoryStorage().setData(SpouseMemory.class, entity2);
        entity2.getMemoryStorage().setData(SpouseMemory.class, entity1);
    }

    protected void clearData(T entity){
        entity.getMemoryStorage().clear(SpouseMemory.class);
        //clear move target
        entity.setMoveTarget(null);
        //clear look target
        entity.setLookTarget(null);
        //reset move speed
        entity.setMovementSpeed(0.1f);
        //interrupt in love status
        entity.getMemoryStorage().get(InLoveMemory.class).setInLove(false);
    }

    protected void updateMove(T entity1, T entity2){
        //clone the vec
        var cloned1 = entity1.clone();
        var cloned2 = entity2.clone();

        //update move target
        entity1.setMoveTarget(cloned2);
        entity2.setMoveTarget(cloned1);

        //update look target
        entity1.setLookTarget(cloned2);
        entity2.setLookTarget(cloned1);
    }

    @Nullable
    protected T getNearestInLove(EntityIntelligent entity){
        var entities = entity.level.getEntities();
        var maxDistanceSquared = -1d;
        T nearestInLove = null;
        for(var e : entities){
            var newDistance = e.distanceSquared(entity);
            if(!e.equals(entity) && entityClass.isInstance(e)){
                T another = (T) e;
                if (!another.isBaby() && another.getMemoryStorage().get(InLoveMemory.class).isInLove() && another.getMemoryStorage().isEmpty(SpouseMemory.class) && (maxDistanceSquared == -1 || newDistance < maxDistanceSquared)) {
                    maxDistanceSquared = newDistance;
                    nearestInLove = another;
                }
            }
        }
        return nearestInLove;
    }

    protected boolean shouldFindingSpouse(T entity){
        return entity.getMemoryStorage().isEmpty(SpouseMemory.class);
    }

    protected void bear(T entity1 ,T entity2){
        var rand = ThreadLocalRandom.current();
        T baby = (T) Entity.createEntity(entity1.getNetworkId(),entity1.getPosition());
        baby.setBaby(true);
        //防止小屁孩去生baby
        baby.getMemoryStorage().setData(InLoveMemory.class, Server.getInstance().getTick());
        baby.spawnToAll();
    }
}
