package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemEgg extends ProjectileItem {
    /**
     * @deprecated 
     */
    

    public ItemEgg() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEgg(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEgg(Integer meta, int count) {
        super(EGG, meta, count, "Egg");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getProjectileEntityType() {
        return EGG;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() { return 16; }
}
