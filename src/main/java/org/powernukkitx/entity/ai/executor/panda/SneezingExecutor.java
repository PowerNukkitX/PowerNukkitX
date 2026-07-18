package org.powernukkitx.entity.ai.executor.panda;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.passive.EntityPanda;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

import java.util.Arrays;

public class SneezingExecutor implements EntityControl, IBehaviorExecutor {

    int ticks = 0;

    @Override
    public boolean execute(EntityIntelligent entity) {
        ticks++;
        return ticks < 20;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        ticks = 0;
        entity.getLevel().addSound(entity, Sound.MOB_PANDA_PRESNEEZE);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SNEEZING);
        entity.getLevel().addSound(entity, Sound.MOB_PANDA_SNEEZE);
        for(Entity entity1 : Arrays.stream(entity.getLevel().getEntities()).filter(entity1 -> entity1 instanceof EntityPanda && entity1.isOnGround() && entity1.distance(entity) < 10).toList()) {
            if(entity1 != entity) ((EntityPanda) entity1).addTmpMoveMotion(new Vector3(0, ((EntityPanda) entity1).getJumpingMotion(1), 0));
        }
        if(Utils.rand(0, 700) == 0) {
            entity.getLevel().dropItem(entity, Item.get(Item.SLIME_BALL));
        }
        entity.setDataFlag(ActorFlags.SNEEZING, false);
    }
}
