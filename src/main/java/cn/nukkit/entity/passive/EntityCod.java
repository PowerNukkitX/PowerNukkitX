package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityCod extends EntityFish {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return COD;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityCod(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    


    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Cod";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.6f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.3f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        //只能25%获得骨头
        if (Utils.rand(0, 3) == 1) {
            return new Item[]{Item.get(Item.BONE, 0, Utils.rand(1, 2)), Item.get(((this.isOnFire()) ? Item.COOKED_COD : Item.COD))};
        }
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_COD : Item.COD))};
    }
}
