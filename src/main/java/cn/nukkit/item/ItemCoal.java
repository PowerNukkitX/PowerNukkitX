package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCoal extends Item {
    /**
     * @deprecated 
     */
    
    public ItemCoal() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCoal(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCoal(Integer meta, int count) {
        super(COAL, meta, count, "Coal");
        if (this.meta == 1) {
            this.name = "Charcoal";
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isCharcoal() {
        return super.getDamage() == 1;
    }
}
