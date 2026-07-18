package org.powernukkitx.entity.ai.executor.villager;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemFood;


public class WillingnessExecutor implements EntityControl, IBehaviorExecutor {


    public WillingnessExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if(entity instanceof EntityVillagerV2 villager) {
            for(int j = 0; j < villager.getInventory().getSize(); j++) {
                Item item = villager.getInventory().getItem(j);
                if(item instanceof ItemFood) {
                    villager.getInventory().clear(j);
                }
            }
        }
        entity.getMemoryStorage().put(CoreMemoryTypes.WILLING, true);
    }
}
