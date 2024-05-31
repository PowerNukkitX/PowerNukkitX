package cn.nukkit.item;

public class ItemChickenSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemChickenSpawnEgg() {
        super(CHICKEN_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 10;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        
    }
}