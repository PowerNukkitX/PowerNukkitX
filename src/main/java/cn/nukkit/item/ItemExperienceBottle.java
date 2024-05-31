package cn.nukkit.item;

import cn.nukkit.entity.EntityID;

public class ItemExperienceBottle extends ProjectileItem {
    /**
     * @deprecated 
     */
    
    public ItemExperienceBottle() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemExperienceBottle(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemExperienceBottle(Integer meta, int count) {
        super(EXPERIENCE_BOTTLE, meta, count, "Bottle o' Enchanting");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getProjectileEntityType() {
        return EntityID.XP_BOTTLE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getThrowForce() {
        return 1f;
    }
}