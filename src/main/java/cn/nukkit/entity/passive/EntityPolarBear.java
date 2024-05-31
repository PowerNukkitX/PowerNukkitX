package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityPolarBear extends EntityAnimal implements EntityWalkable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return POLAR_BEAR;
    }
    /**
     * @deprecated 
     */
    

    public EntityPolarBear(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (this.isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (this.isBaby()) {
            return 0.7f;
        }
        return 1.4f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.COD), Item.get(Item.SALMON)};
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Polar Bear";
    }
}
