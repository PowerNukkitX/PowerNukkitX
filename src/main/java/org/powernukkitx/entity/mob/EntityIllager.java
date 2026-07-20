package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public abstract class EntityIllager extends EntityMob implements EntityWalkable {
    public EntityIllager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case VILLAGER ->
                    entity instanceof EntityVillagerV2 villager && !villager.isBaby();
            case IRON_GOLEM, WANDERING_TRADER -> true;
            default -> super.attackTarget(entity);
        };
    }
}
