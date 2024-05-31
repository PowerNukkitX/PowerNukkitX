package cn.nukkit.item;

public class ItemTropicalFish extends ItemFish {
    /**
     * @deprecated 
     */
    
    public ItemTropicalFish() {
        super(TROPICAL_FISH, 0, 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 0.2F;
    }
}