package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
