package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;

import java.awt.*;

public class EffectAbsorption extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectAbsorption() {
        super(EffectType.ABSORPTION, "%potion.absorption", new Color(37, 82, 165));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void add(Entity entity) {
        int $1 = (4 * this.getLevel());
        if (absorption > entity.getAbsorption()){
            entity.setAbsorption(absorption);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void remove(Entity entity) {
        entity.setAbsorption(0);
    }
}
