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
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

import java.util.Arrays;

public class VillagerBreedingExecutor extends BreedingExecutor {
    public VillagerBreedingExecutor(int findingRange, int duration, float moveSpeed) {
        super(findingRange, duration, moveSpeed);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        // Villagers don't use BreedableComponent mate search
        if (entity == null || entity.level == null) return false;

        if (!isBreeding(entity)) {
            EntityIntelligent spouse = getSpouse(entity);
            if (spouse == null) return false;

            entity.setMovementSpeed(moveSpeed);
            spouse.setMovementSpeed(moveSpeed);

            setBreeding(entity, true);
            setBreedingTick(entity, 0);
            setBreeding(spouse, true);
            setBreedingTick(spouse, 0);
        }

        EntityIntelligent spouse = getSpouse(entity);
        if (spouse == null) {
            clearBreedingState(entity);
            return false;
        }

        updateMove(entity, spouse);

        if (!isBreedingLeader(entity, spouse)) return true;

        int currentTick = getBreedingTick(entity) + 1;
        setBreedingTick(entity, currentTick);

        if (currentTick > duration) {
            breed(entity, spouse);

            clearData(entity);
            clearData(spouse);

            clearBreedingState(entity);
            clearBreedingState(spouse);

            entity.setEnablePitch(false);
            spouse.setEnablePitch(false);

            return false;
        }

        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        EntityIntelligent spouse = entity != null ? getSpouse(entity) : null;

        if (entity != null) {
            clearData(entity);
            clearBreedingState(entity);
            entity.setEnablePitch(false);
        }

        if (spouse != null) {
            clearData(spouse);
            clearBreedingState(spouse);
            spouse.setEnablePitch(false);
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
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(entity.getId());
        pk.setType(ActorEvent.LOVE_HEARTS);
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    protected void sendAngryParticles(EntityIntelligent entity) {
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(entity.getId());
        pk.setType(ActorEvent.VILLAGER_ANGRY);
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}