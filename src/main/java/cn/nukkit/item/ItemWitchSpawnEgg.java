package cn.nukkit.item;

public class ItemWitchSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemWitchSpawnEgg() {
        super(WITCH_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 45;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}