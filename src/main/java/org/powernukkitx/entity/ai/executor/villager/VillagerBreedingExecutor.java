package org.powernukkitx.entity.ai.executor.villager;

import org.powernukkitx.Server;
import org.powernukkitx.block.BlockBed;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.level.village.Village;
import org.powernukkitx.level.village.VillagePoi;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

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
        var villageManager = parent1.getLevel().getVillageManager();
        Village village = villageManager.getVillageForDweller(parent1.getId()).orElse(null);
        if (village == null || villageManager.getVillageForDweller(parent2.getId())
                .filter(other -> other.uuid().equals(village.uuid())).isEmpty()) {
            sendAngryParticles(parent1);
            sendAngryParticles(parent2);
            return;
        }

        VillagePoi home = villageManager.findClosestAvailableHome(village.uuid(), parent1).orElse(null);
        if (home == null || !(parent1.getLevel().getBlock(home.position().x, home.position().y,
                home.position().z, false) instanceof BlockBed bed) || !bed.isBedValid() || bed.isOccupied()) {
            sendAngryParticles(parent1);
            sendAngryParticles(parent2);
            return;
        }

        EntityVillagerV2 baby = (EntityVillagerV2) Entity.createEntity(parent1.getNetworkId(), parent1.getPosition());
        if (baby == null) return;

        baby.setBaby(true);
        baby.setBed(bed.getFootPart());
        if (baby.getBed() == null || !village.uuid().equals(baby.getVillageUuid())) {
            baby.close();
            sendAngryParticles(parent1);
            sendAngryParticles(parent2);
            return;
        }

        sendInLoveParticles(parent1);
        sendInLoveParticles(parent2);

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
