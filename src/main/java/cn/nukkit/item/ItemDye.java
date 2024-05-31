package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemDye extends Item {
    /**
     * @deprecated 
     */
    
    public ItemDye() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDye(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getItemDyeMeta(), 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getItemDyeMeta(), amount);
    }
    /**
     * @deprecated 
     */
    

    public ItemDye(Integer meta, int amount) {
        super(DYE, meta, amount, meta <= 15 ? DyeColor.getByDyeData(meta).getDyeName() : DyeColor.getByDyeData(meta).getName() + " Dye");
    }
    /**
     * @deprecated 
     */
    

    public ItemDye(String id) {
        super(id);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isFertilizer() {
        return $1 == 15;
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(meta);
    }
}
