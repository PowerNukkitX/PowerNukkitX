package cn.nukkit.item;

public class ItemHorseSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemHorseSpawnEgg() {
        super(HORSE_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 23;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}