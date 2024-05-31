package cn.nukkit.item;

public class ItemPhantomSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemPhantomSpawnEgg() {
        super(PHANTOM_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 58;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}