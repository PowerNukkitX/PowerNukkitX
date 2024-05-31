package cn.nukkit.item;

public class ItemPiglinSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemPiglinSpawnEgg() {
        super(PIGLIN_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 123;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}