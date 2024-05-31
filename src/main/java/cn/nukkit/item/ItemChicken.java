package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;

public class ItemChicken extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemChicken() {
        super(CHICKEN, 0, 1, "Raw Chicken");
    }
    /**
     * @deprecated 
     */
    

    public ItemChicken(int count) {
        super(CHICKEN, 0, count, "Raw Chicken");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 2;
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
        if(0.3F >= Math.random()) {
            player.addEffect(Effect.get(EffectType.HUNGER).setDuration(30 * 20));
        }

        return true;
    }

}