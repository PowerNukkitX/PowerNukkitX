package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.Sound;


public class WardenEmergingAnimationExecutor implements IBehaviorExecutor {

    protected int duration;
    protected int currentTick;

    public WardenEmergingAnimationExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        return currentTick <= duration;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.currentTick = 0;
        entity.setDataFlag(EntityFlag.EMERGING, false);
        entity.setDataFlagExtend(EntityFlag.EMERGING, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.getLevel().addSound(entity, Sound.MOB_WARDEN_EMERGE);
        entity.setMoveTarget(null);

        entity.setDataFlag(EntityFlag.EMERGING, true);
        entity.setDataFlagExtend(EntityFlag.EMERGING, true);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.currentTick = 0;
        entity.setDataFlag(EntityFlag.EMERGING, false);
        entity.setDataFlagExtend(EntityFlag.EMERGING, false);
    }
}
