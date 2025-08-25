package cn.nukkit.entity.ai.executor.panda;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.passive.EntityPanda;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

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
        entity.setDataFlag(EntityFlag.SNEEZING);
        entity.getLevel().addSound(entity, Sound.MOB_PANDA_SNEEZE);
        for(Entity entity1 : Arrays.stream(entity.getLevel().getEntities()).filter(entity1 -> entity1 instanceof EntityPanda && entity1.isOnGround() && entity1.distance(entity) < 10).toList()) {
            if(entity1 != entity) ((EntityPanda) entity1).addTmpMoveMotion(new Vector3(0, ((EntityPanda) entity1).getJumpingMotion(1), 0));
        }
        if(Utils.rand(0, 700) == 0) {
            entity.getLevel().dropItem(entity, Item.get(Item.SLIME_BALL));
        }
        entity.setDataFlag(EntityFlag.SNEEZING, false);
    }
}
