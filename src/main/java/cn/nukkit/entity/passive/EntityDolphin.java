package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityDolphin extends EntityAnimal implements EntitySwimmable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return DOLPHIN;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityDolphin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    


    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Dolphin";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.6f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.COD)};
    }
}
