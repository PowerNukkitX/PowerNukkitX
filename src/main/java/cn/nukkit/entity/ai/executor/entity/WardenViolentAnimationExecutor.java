package cn.nukkit.entity.ai.executor.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.evaluator.NewAttackTargetMemory;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.AttackTargetMemory;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class WardenViolentAnimationExecutor implements IBehaviorExecutor {

    protected int duration;
    protected int currentTick;

    public WardenViolentAnimationExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (currentTick > duration) return false;
        else {
            //更新视线target
            if (entity.getMemoryStorage().notEmpty(AttackTargetMemory.class))
                entity.setLookTarget(entity.getMemoryStorage().get(AttackTargetMemory.class).getData());
            return true;
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.currentTick = 0;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ROARING, false);
        entity.setDataFlag(Entity.DATA_FLAGS_EXTENDED, Entity.DATA_FLAG_ROARING, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.getMemoryStorage().setData(NewAttackTargetMemory.class, false);
        entity.setMoveTarget(null);

        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ROARING, true);
        entity.setDataFlag(Entity.DATA_FLAGS_EXTENDED, Entity.DATA_FLAG_ROARING, true);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.currentTick = 0;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ROARING, false);
        entity.setDataFlag(Entity.DATA_FLAGS_EXTENDED, Entity.DATA_FLAG_ROARING, false);
    }
}
