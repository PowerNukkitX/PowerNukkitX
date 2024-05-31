package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityZombieHorse extends EntityAnimal implements EntityWalkable, EntitySmite {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return ZOMBIE_HORSE;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityZombieHorse(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 1.4f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.6f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(15);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.ROTTEN_FLESH, 1, 1)};
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isUndead() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Zombie Horse";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
