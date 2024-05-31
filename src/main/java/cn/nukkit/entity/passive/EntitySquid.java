package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntitySquid extends EntityAnimal implements EntitySwimmable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SQUID;
    }
    /**
     * @deprecated 
     */
    

    public EntitySquid(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.95f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.95f;
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
        return new Item[]{ Item.get(ItemID.INK_SAC, 0, 1) };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Squid";
    }
}
