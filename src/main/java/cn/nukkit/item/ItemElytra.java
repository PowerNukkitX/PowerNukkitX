package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemElytra extends ItemArmor {
    /**
     * @deprecated 
     */
    

    public ItemElytra() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemElytra(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemElytra(Integer meta, int count) {
        super(ELYTRA, meta, count, "Elytra");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 433;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isArmor() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isChestplate() {
        return true;
    }
}
