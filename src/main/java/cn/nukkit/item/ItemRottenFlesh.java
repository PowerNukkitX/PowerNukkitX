package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRottenFlesh extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemRottenFlesh() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRottenFlesh(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRottenFlesh(Integer meta, int count) {
        super(ROTTEN_FLESH, meta, count, "Rotten Flesh");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 0.8F;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onEaten(Player player) {
        if(0.8F >= Math.random()) {
            player.addEffect(Effect.get(EffectType.HUNGER).setDuration(30 * 20));
        }

        return true;
    }
}
