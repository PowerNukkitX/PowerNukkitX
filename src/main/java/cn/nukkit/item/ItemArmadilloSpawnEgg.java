package cn.nukkit.item;

public class ItemArmadilloSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
     public ItemArmadilloSpawnEgg() {
         super(ARMADILLO_SPAWN_EGG);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return 142;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}