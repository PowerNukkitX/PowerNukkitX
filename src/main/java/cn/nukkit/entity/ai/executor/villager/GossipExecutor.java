package cn.nukkit.entity.ai.executor.villager;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GossipExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    private static final int STAY_TICKS = 60;

    private final MemoryType<? extends EntityVillagerV2> type;

    @Override
    public boolean execute(EntityIntelligent entity) {
        EntityVillagerV2 entity1 = entity.getMemoryStorage().get(type);
        if(entity1 != null) {
            if(entity instanceof EntityVillagerV2 villager) {
                if(entity1.isHungry() && villager.shouldShareFood()) {
                    for(int i = 0; i < villager.getInventory().getSize(); i++) {
                        Item item = villager.getInventory().getUnclonedItem(i);
                        item.setCount(item.getCount()/2);
                        villager.getLevel().dropItem(villager.getPosition().add(0, villager.getEyeHeight(), 0), item, new Vector3(entity1.x - entity.x, entity1.y - entity.y, entity1.z - entity.z).normalize().divide(2));
                    }
                }
            }
        } else return false;
        return tick < 100;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.tick = 0;
        entity.getMemoryStorage().put(CoreMemoryTypes.LAST_GOSSIP, entity.getLevel().getTick());
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.getMemoryStorage().clear(type);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
