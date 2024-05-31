package cn.nukkit.item;

import cn.nukkit.Player;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRabbitStew extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemRabbitStew() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRabbitStew(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRabbitStew(Integer meta, int count) {
        super(RABBIT_STEW, meta, count, "Rabbit Stew");
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
        return 10;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 12F;
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
