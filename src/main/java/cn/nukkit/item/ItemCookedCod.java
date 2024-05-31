package cn.nukkit.item;

public class ItemCookedCod extends ItemCod {
    /**
     * @deprecated 
     */
    
    public ItemCookedCod() {
        super(COOKED_COD, 0, 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 6F;
    }
}