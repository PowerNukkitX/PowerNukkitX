package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;

import cn.nukkit.entity.mob.EntityShulker;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Utils;


public class ShulkerIdleExecutor implements IBehaviorExecutor {

    private int stayTicks = 0;
    private int tick = 0;

    public ShulkerIdleExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if(tick >= stayTicks) return false;
        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        tick = 0;
        stayTicks = Utils.rand(20, 61);
        if(entity instanceof EntityShulker shulker) {
            shulker.setPeeking(30);
            shulker.getLevel().addSound(shulker, Sound.MOB_SHULKER_OPEN);
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if(entity instanceof EntityShulker shulker) {
            shulker.setPeeking(0);
            shulker.getLevel().addSound(shulker, Sound.MOB_SHULKER_CLOSE);
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
