package cn.nukkit.entity.effect;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;

import java.awt.*;

public class EffectHunger extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectHunger() {
        super(EffectType.HUNGER, "%potion.hunger", new Color(88, 118, 83));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canTick() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void apply(Entity entity, double tickCount) {
        if (entity instanceof Player player) {
            player.getFoodData().exhaust(0.1 * this.getLevel());
        }
    }
}
