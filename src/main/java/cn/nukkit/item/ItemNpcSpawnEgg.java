package cn.nukkit.item;

public class ItemNpcSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemNpcSpawnEgg() {
        super(NPC_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 51;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}