package cn.nukkit.entity.ai.executor.villager;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Arrays;


public class VillagerBreedingExecutor extends BreedingExecutor {

    public VillagerBreedingExecutor(int findingRange, int duration, float moveSpeed) {
        super(findingRange, duration, moveSpeed);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        // Villagers don't use BreedableComponent mate search
        if (entity == null || entity.level == null) return false;
        if (!finded) {
            Object spouse = entity.getMemoryStorage().get(CoreMemoryTypes.ENTITY_SPOUSE);
            if (!(spouse instanceof EntityIntelligent s)) return false;

            another = s;
            finded = true;

            entity.setMovementSpeed(moveSpeed);
            another.setMovementSpeed(moveSpeed);
        }

        if (another == null) return false;

        currentTick++;

        updateMove(entity, another);

        if (currentTick > duration) {
            breed(entity, another);

            clearData(entity);
            clearData(another);

            currentTick = 0;
            finded = false;

            entity.setEnablePitch(false);
            another.setEnablePitch(false);
            another = null;

            return false;
        }

        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        if (entity != null) {
            clearData(entity);
            entity.setEnablePitch(false);
        }

        currentTick = 0;
        finded = false;

        if (another != null) {
            clearData(another);
            another.setEnablePitch(false);
            another = null;
        }
    }

    @Override
    protected void breed(EntityIntelligent parent1, EntityIntelligent parent2) {
        int range = 48;
        int lookY = 5;
        BlockBed block = null;

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -lookY; y <= lookY; y++) {
                    Location lookLocation = parent1.add(x, y, z);
                    Block lookBlock = lookLocation.getLevelBlock();

                    if (lookBlock instanceof BlockBed bed) {
                        boolean occupied = Arrays.stream(parent1.getLevel().getEntities())
                                .anyMatch(e -> e instanceof EntityVillagerV2 v
                                        && v.getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED)
                                        && v.getBed().equals(bed));

                        if (!bed.isHeadPiece() && !occupied) {
                            block = bed.getFootPart();
                            break;
                        }
                    }
                }
                if (block != null) break;
            }
            if (block != null) break;
        }

        if (block == null) {
            sendAngryParticles(parent1);
            sendAngryParticles(parent2);
            return;
        } else {
            sendInLoveParticles(parent1);
            sendInLoveParticles(parent2);
        }

        EntityVillagerV2 baby = (EntityVillagerV2) Entity.createEntity(parent1.getNetworkId(), parent1.getPosition());
        if (baby == null) return;

        baby.setBaby(true);

        // prevent baby breeding instantly
        baby.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, parent1.level.getTick());
        baby.getMemoryStorage().put(CoreMemoryTypes.PARENT, parent1);

        baby.spawnToAll();
    }

    @Override
    protected void clearData(EntityIntelligent entity) {
        entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);

        entity.setMoveTarget(null);
        entity.setLookTarget(null);

        entity.setMovementSpeed(0.1f);

        entity.getMemoryStorage().put(CoreMemoryTypes.WILLING, false);
        entity.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, entity.getLevel().getTick());
    }

    protected void sendInLoveParticles(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.LOVE_PARTICLES;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    protected void sendAngryParticles(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.VILLAGER_ANGRY;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}