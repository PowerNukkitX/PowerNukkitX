package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityParrot extends EntityAnimal implements EntityFlyable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return PARROT;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityParrot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    


    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Parrot";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.0f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.FEATHER)};
    }
}
