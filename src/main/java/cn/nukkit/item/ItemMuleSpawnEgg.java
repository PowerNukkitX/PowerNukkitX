package cn.nukkit.item;

public class ItemMuleSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemMuleSpawnEgg() {
        super(MULE_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 25;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}