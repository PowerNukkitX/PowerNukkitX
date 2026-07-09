package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.EntitySmite;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @deprecated Zombie Villagers were replaced by {@link EntityZombieVillagerV2} in 1.14
 */
@Deprecated
public class EntityZombieVillager extends EntityZombie implements EntityWalkable, EntitySmite {

    @Override
    @NotNull
    public String getIdentifier() {
        return ZOMBIE_VILLAGER;
    }

    public EntityZombieVillager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Zombie Villager";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombie", "zombie_villager", "undead", "monster", "mob");
    }

}
