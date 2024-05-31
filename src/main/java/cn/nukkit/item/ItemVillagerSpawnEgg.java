package cn.nukkit.item;

public class ItemVillagerSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemVillagerSpawnEgg() {
        super(VILLAGER_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 115;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}