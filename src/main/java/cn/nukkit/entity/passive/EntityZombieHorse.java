package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityZombieHorse extends EntityAnimal implements EntityWalkable, EntitySmite {
    @Override
    @NotNull public String getIdentifier() {
        return ZOMBIE_HORSE;
    }
    

    public EntityZombieHorse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 1.6f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.ROTTEN_FLESH, 1, 1)};
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Zombie Horse";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("zombiehorse", "undead", "mob");
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
