package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.math.Vector3;

/**
 * @author joserobjr
 */
public class ItemHoneyBottle extends ItemFood {
    /**
     * @deprecated 
     */
    
    
    public ItemHoneyBottle() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    
    
    public ItemHoneyBottle(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    
    
    public ItemHoneyBottle(Integer meta, int count) {
        super(HONEY_BOTTLE, meta, count, "Honey Bottle");
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 16;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
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
        return 1.2F;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onEaten(Player player) {
        player.getInventory().addItem(new ItemGlassBottle());
        player.removeEffect(EffectType.POISON);

        return true;
    }
}
