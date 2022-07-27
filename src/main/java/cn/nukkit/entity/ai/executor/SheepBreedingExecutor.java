package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.InLoveMemory;
import cn.nukkit.entity.ai.memory.SpouseMemory;
import cn.nukkit.entity.passive.EntitySheep;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SheepBreedingExecutor implements IBehaviorExecutor{

    protected int findingRangeSquared;
    protected int duration;
    protected int currentTick = 0;
    protected int tryFindingTime;
    protected int currentTryFindingTime = 0;
    protected float moveSpeed;
    protected boolean finded;
    protected EntitySheep anotherSheep;

    public SheepBreedingExecutor(int findingRangeSquared,int tryFindingTime, int duration, float moveSpeed) {
        this.findingRangeSquared = findingRangeSquared;
        this.duration = duration;
        this.tryFindingTime = tryFindingTime;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        //must to be a sheep
        if (entity instanceof EntitySheep entitySheep) {
            if (shouldFindingSpouse(entity)) {
                currentTryFindingTime++;
                if (currentTryFindingTime > tryFindingTime) {
                    //failed to find a spouse
                    currentTryFindingTime = 0;
                    return false;
                }
                anotherSheep = getNearestInLoveSheep(entity);
                if (anotherSheep == null) return true;
                anotherSheep.getMemoryStorage().setData(SpouseMemory.class, entity);
                entity.getMemoryStorage().setData(SpouseMemory.class, anotherSheep);

                //set move speed
                entity.setMovementSpeed(moveSpeed);
                anotherSheep.setMovementSpeed(moveSpeed);

                finded = true;
            }
            if (finded) {
                currentTick++;

                //clone the vec
                var clonedSheepVec = entity.clone();
                var clonedAnotherSheepVec = anotherSheep.clone();

                //update move target
                entity.setMoveTarget(clonedAnotherSheepVec);
                anotherSheep.setMoveTarget(clonedSheepVec);

                //update look target
                entity.setLookTarget(clonedAnotherSheepVec);
                anotherSheep.setLookTarget(clonedSheepVec);

                if (currentTick > duration) {
                    bear(entitySheep, anotherSheep);
                    entity.getMemoryStorage().clear(SpouseMemory.class);
                    anotherSheep.getMemoryStorage().clear(SpouseMemory.class);

                    //clear move target
                    entity.setMoveTarget(null);
                    anotherSheep.setMoveTarget(null);

                    //clear look target
                    entity.setLookTarget(null);
                    anotherSheep.setLookTarget(null);

                    //clear move speed
                    entity.setMovementSpeed(0.1f);
                    anotherSheep.setMovementSpeed(0.1f);

                    //interrupt in love status
                    entity.getMemoryStorage().get(InLoveMemory.class).setInLove(false);
                    anotherSheep.getMemoryStorage().get(InLoveMemory.class).setInLove(false);

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

    @Nullable
    protected EntitySheep getNearestInLoveSheep(EntityIntelligent entity){
        var entities = entity.level.getEntities();
        var maxDistanceSquared = -1d;
        EntitySheep nearestInLoveSheep = null;
        for(var e : entities){
            var newDistance = e.distanceSquared(entity);
            if(!e.equals(entity) && e instanceof EntitySheep anotherSheep && !anotherSheep.isBaby() && anotherSheep.getMemoryStorage().get(InLoveMemory.class).isInLove() && anotherSheep.getMemoryStorage().isEmpty(SpouseMemory.class) && (maxDistanceSquared == -1 || newDistance < maxDistanceSquared)){
                maxDistanceSquared = newDistance;
                nearestInLoveSheep = anotherSheep;
            }
        }
        return nearestInLoveSheep;
    }

    protected boolean shouldFindingSpouse(EntityIntelligent entity){
        return entity.getMemoryStorage().isEmpty(SpouseMemory.class);
    }

    protected void bear(EntitySheep sheep1, EntitySheep sheep2){
        var rand = ThreadLocalRandom.current();
        EntitySheep baby = new EntitySheep(sheep1.getChunk(),Entity.getDefaultNBT(sheep1));
        baby.setColor(rand.nextBoolean() ? sheep1.getColor() : sheep2.getColor());
        baby.setBaby(true);
        //防止小屁孩去生baby
        baby.getMemoryStorage().setData(InLoveMemory.class, Server.getInstance().getTick());
        baby.spawnToAll();
    }
}
