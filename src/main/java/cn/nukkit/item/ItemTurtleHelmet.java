package cn.nukkit.item;

public class ItemTurtleHelmet extends Item {
    /**
     * @deprecated 
     */
    

    public ItemTurtleHelmet() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemTurtleHelmet(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemTurtleHelmet(Integer meta, int count) {
        super(TURTLE_HELMET, meta, count, "Turtle Shell");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_OTHER;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isHelmet() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 276;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEnchantAbility() {
        return 9;
    }
}