package cn.nukkit.item;

public class ItemGhastSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemGhastSpawnEgg() {
        super(GHAST_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 41;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}