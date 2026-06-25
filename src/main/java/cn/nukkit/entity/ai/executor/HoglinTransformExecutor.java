package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityZoglin;
import cn.nukkit.event.entity.EntityTransformEvent;
import cn.nukkit.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

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
        entity.setDataFlag(ActorFlags.SHAKING);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SHAKING, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

    private void transform(EntityIntelligent entity) {
        entity.saveNBT();
        EntityZoglin zoglin = new EntityZoglin(entity.getChunk(), entity.getNbt());
        EntityTransformEvent event = new EntityTransformEvent(entity, zoglin);
        Server.getInstance().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            zoglin.close();
        } else {
            entity.close();
            zoglin.spawnToAll();
            zoglin.level.addSound(zoglin, Sound.MOB_HOGLIN_CONVERTED_TO_ZOMBIFIED);
            zoglin.addEffect(Effect.get(EffectType.NAUSEA).setDuration(15));
        }
    }

}


