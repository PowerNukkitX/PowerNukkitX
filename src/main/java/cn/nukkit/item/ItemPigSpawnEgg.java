package cn.nukkit.item;

public class ItemPigSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemPigSpawnEgg() {
        super(PIG_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 12;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}