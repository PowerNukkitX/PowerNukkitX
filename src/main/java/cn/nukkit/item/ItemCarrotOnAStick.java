package cn.nukkit.item;

/**
 * @author lion
 * @since 21.03.17
 */
public class ItemCarrotOnAStick extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemCarrotOnAStick() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCarrotOnAStick(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCarrotOnAStick(Integer meta, int count) {
        super(CARROT_ON_A_STICK, meta, count, "Carrot on a Stick");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CARROT_ON_A_STICK;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean noDamageOnBreak() {
        return true;
    }
}

