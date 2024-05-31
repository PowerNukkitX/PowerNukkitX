package cn.nukkit.item;

public class ItemDrownedSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemDrownedSpawnEgg() {
        super(DROWNED_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 110;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
    }
}