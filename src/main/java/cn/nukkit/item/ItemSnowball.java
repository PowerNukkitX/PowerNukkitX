package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSnowball extends ProjectileItem {
    /**
     * @deprecated 
     */
    

    public ItemSnowball() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSnowball(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSnowball(Integer meta, int count) {
        super(SNOWBALL, 0, count, "Snowball");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getProjectileEntityType() {
        return SNOWBALL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getThrowForce() {
        return 1.5f;
    }
}
