package cn.nukkit.entity.ai.executor.villager;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class GossipExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;
    protected boolean spread = false;
    private static final int STAY_TICKS = 60;

    private final MemoryType<? extends EntityVillagerV2> type;

    @Override
    public boolean execute(EntityIntelligent entity) {
        EntityVillagerV2 entity1 = entity.getMemoryStorage().get(type);
        if(entity1 != null) {
            if(entity1.toHorizontal().distance(entity.toHorizontal()) < 2) {
                if(!spread) {
                    removeRouteTarget(entity);
                    if(entity instanceof EntityVillagerV2 villager) {
                        villager.spreadGossip();
                        spread = true;
                    }
                }
                if(entity instanceof EntityVillagerV2 villager) {
                    if(entity1.isHungry() && villager.shouldShareFood()) {
                        for(int i = 0; i < villager.getInventory().getSize(); i++) {
                            Item item = villager.getInventory().getUnclonedItem(i);
                            item.setCount(item.getCount()/2);
                            if(item.getId() == Block.WHEAT) item = Item.get(Block.WHEAT, 0, item.getCount()/3);
                            villager.getLevel().dropItem(villager.getPosition().add(0, villager.getEyeHeight(), 0), item, new Vector3(entity1.x - entity.x, entity1.y - entity.y, entity1.z - entity.z).normalize().multiply(0.4));
                        }
                    }
                }
            }
            if(tick % 100 == 0) {
                if(Utils.rand(0, 10) == 0) {
                    Arrays.stream(entity.getLevel().getCollidingEntities(entity.getBoundingBox().grow(2, 0, 2))).filter(entity2 -> entity2 instanceof EntityVillagerV2 && entity2 != entity).map(entity2 -> ((EntityVillagerV2) entity2)).forEach(entity2 -> entity2.setLookTarget(entity));
                }
            }
        }
        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.setMoveTarget(entity.getMemoryStorage().get(type));
        entity.setLookTarget(entity.getMemoryStorage().get(type));
        this.tick = 0;
        this.spread = false;
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
