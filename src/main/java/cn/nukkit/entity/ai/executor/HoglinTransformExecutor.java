package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityZoglin;
import cn.nukkit.level.Sound;

public class HoglinTransformExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    public HoglinTransformExecutor() {}

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if(tick >= 300) {
            transform(entity);
            return false;
        }
        return true;
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        tick = -1;
        entity.setDataFlag(EntityFlag.SHAKING);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(EntityFlag.SHAKING, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

    private void transform(EntityIntelligent entity) {
        entity.saveNBT();
        entity.close();
        EntityZoglin zoglin = new EntityZoglin(entity.getChunk(), entity.namedTag);
        zoglin.setPosition(entity);
        zoglin.setRotation(entity.yaw, entity.pitch);
        zoglin.setBaby(entity.isBaby());
        zoglin.spawnToAll();
        zoglin.level.addSound(zoglin, Sound.MOB_HOGLIN_CONVERTED_TO_ZOMBIFIED);
        zoglin.addEffect(Effect.get(EffectType.NAUSEA).setDuration(15));
    }

}


