package org.powernukkitx.entity.ai.executor.armadillo;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.utils.Utils;

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
