package cn.nukkit.entity.ai.executor.villager;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Arrays;

public class VillagerBreedingExecutor extends EntityBreedingExecutor {

    public VillagerBreedingExecutor(Class entityClass, int findingRangeSquared, int duration, float moveSpeed) {
        super(entityClass, findingRangeSquared, duration, moveSpeed);
    }

    @Override
    protected void bear(EntityIntelligent entity) {

        int range = 48;
        int lookY = 5;
        BlockBed block = null;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -lookY; y <= lookY; y++) {
                    Location lookLocation = entity.add(x, y, z);
                    Block lookBlock = lookLocation.getLevelBlock();
                    if (lookBlock instanceof BlockBed bed) {
                        if (!bed.isHeadPiece() && Arrays.stream(entity.getLevel().getEntities()).noneMatch(entity1 -> entity1 instanceof EntityVillagerV2 v && v.getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED) && v.getBed().equals(bed))) {
                            block = bed.getFootPart();
                        }
                    }
                }
            }
        }
        if(block == null) {
            sendAngryParticles(entity);
            return;
        }

        EntityVillagerV2 baby = (EntityVillagerV2) Entity.createEntity(entity.getNetworkId(), entity.getPosition());
        baby.setBaby(true);
        //防止小屁孩去生baby
        baby.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, entity.level.getTick());
        baby.getMemoryStorage().put(CoreMemoryTypes.PARENT, entity);
        baby.spawnToAll();
    }


    protected void sendAngryParticles(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.VILLAGER_ANGRY;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
