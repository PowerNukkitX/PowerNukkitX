package cn.nukkit.entity.ai.executor.armadillo;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityArmadillo;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Utils;

public class ShedExecutor implements EntityControl, IBehaviorExecutor {

    public ShedExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.getLevel().dropItem(entity, Item.get(Item.ARMADILLO_SCUTE));
        entity.getLevel().addSound(entity, Sound.MOB_ARMADILLO_SCUTE_DROP);
        entity.getMemoryStorage().put(CoreMemoryTypes.NEXT_SHED, entity.getLevel().getTick() + Utils.rand(6_000, 10_800));
    }
}
