package cn.nukkit.item;

import cn.nukkit.Player;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemMushroomStew extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemMushroomStew() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemMushroomStew(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemMushroomStew(Integer meta, int count) {
        super(MUSHROOM_STEW, 0, count, "Mushroom Stew");
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
    
    public int getFoodRestore() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 7.2F;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onEaten(Player player) {
        player.getInventory().addItem(new ItemBowl());

        return true;
    }
}
