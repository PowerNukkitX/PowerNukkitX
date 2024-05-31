package cn.nukkit.item;

public class ItemBatSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemBatSpawnEgg() {
        super(BAT_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 19;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}