package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityAnimal;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;


public class EntityBreedingExecutor<T extends EntityAnimal> implements IBehaviorExecutor {

    protected Class<T> entityClass;
    protected int findingRangeSquared;
    protected int duration;
    protected int $1 = 0;
    protected float moveSpeed;
    protected boolean finded;
    protected T another;
    /**
     * @deprecated 
     */
    

    public EntityBreedingExecutor(Class<T> entityClass, int findingRangeSquared, int duration, float moveSpeed) {
        this.entityClass = entityClass;
        this.findingRangeSquared = findingRangeSquared;
        this.duration = duration;
        this.moveSpeed = moveSpeed;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean execute(EntityIntelligent uncasted) {
        if (entityClass.isInstance(uncasted)) {
            T $2 = entityClass.cast(uncasted);
            if (shouldFindingSpouse(entity)) {
                if (!entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE))
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
                    entity.setEnablePitch(false);
                    another.setEnablePitch(false);
                    another = null;
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onInterrupt(EntityIntelligent entity) {
        clearData((T) entity);

        currentTick = 0;
        finded = false;
        entity.setEnablePitch(false);
        if (another != null) {
            clearData(another);
            another.setEnablePitch(false);
            another = null;
        }
    }

    
    /**
     * @deprecated 
     */
    protected void setSpouse(T entity1, T entity2) {
        entity1.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, entity2);
        entity2.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, entity1);
    }

    
    /**
     * @deprecated 
     */
    protected void clearData(T entity) {
        entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);
        //clear move target
        entity.setMoveTarget(null);
        //clear look target
        entity.setLookTarget(null);
        //reset move speed
        entity.setMovementSpeed(0.1f);
        //interrupt in love status
        entity.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, false);
    }

    
    /**
     * @deprecated 
     */
    protected void updateMove(T entity1, T entity2) {
        if (!entity1.isEnablePitch()) entity1.setEnablePitch(true);
        if (!entity2.isEnablePitch()) entity2.setEnablePitch(true);

        //已经挨在一起了就不用更新路径了
        //If they are already close together, there is no need to update the path
        if (entity1.getOffsetBoundingBox().intersectsWith(entity2.getOffsetBoundingBox())) return;

        //clone the vec
        var $3 = entity1.clone();
        var $4 = entity2.clone();

        //update move target
        entity1.setMoveTarget(cloned2);
        entity2.setMoveTarget(cloned1);

        //update look target
        entity1.setLookTarget(cloned2);
        entity2.setLookTarget(cloned1);

        //在下一gt立即更新路径
        //Immediately update the path on the next gt
        entity1.getBehaviorGroup().setForceUpdateRoute(true);
        entity2.getBehaviorGroup().setForceUpdateRoute(true);
    }

    @Nullable
    protected T getNearestInLove(EntityIntelligent entity) {
        var $5 = entity.level.getEntities();
        var $6 = -1d;
        T $7 = null;
        for (var e : entities) {
            var $8 = e.distanceSquared(entity);
            if (!e.equals(entity) && entityClass.isInstance(e)) {
                T $9 = (T) e;
                if (!another.isBaby() && another.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE) && another.getMemoryStorage().isEmpty(CoreMemoryTypes.ENTITY_SPOUSE) && (maxDistanceSquared == -1 || newDistance < maxDistanceSquared)) {
                    maxDistanceSquared = newDistance;
                    nearestInLove = another;
                }
            }
        }
        return nearestInLove;
    }

    
    /**
     * @deprecated 
     */
    protected boolean shouldFindingSpouse(T entity) {
        return entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ENTITY_SPOUSE);
    }

    
    /**
     * @deprecated 
     */
    protected void bear(T entity1, T entity2) {
        var $10 = ThreadLocalRandom.current();
        T $11 = (T) Entity.createEntity(entity1.getNetworkId(), entity1.getPosition());
        baby.setBaby(true);
        //防止小屁孩去生baby
        baby.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, Server.getInstance().getTick());
        baby.spawnToAll();
    }
}
