package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityRabbit extends EntityAnimal implements EntityWalkable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return RABBIT;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityRabbit(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.67f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.67f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Rabbit";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_RABBIT : Item.RABBIT)), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT)};
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }
}
