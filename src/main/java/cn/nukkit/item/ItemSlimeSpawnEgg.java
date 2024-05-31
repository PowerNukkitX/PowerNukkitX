package cn.nukkit.item;

public class ItemSlimeSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemSlimeSpawnEgg() {
        super(SLIME_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 37;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}