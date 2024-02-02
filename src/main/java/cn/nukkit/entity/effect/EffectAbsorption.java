package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;

import java.awt.*;

public class EffectAbsorption extends Effect {

    public EffectAbsorption() {
        super(EffectType.ABSORPTION, "%potion.absorption", new Color(37, 82, 165));
    }

    @Override
    public void add(Entity entity) {
        int absorption = (4 * this.getLevel());
        if (absorption > entity.getAbsorption()){
            entity.setAbsorption(absorption);
        }
    }

    @Override
    public void remove(Entity entity) {
        entity.setAbsorption(0);
    }
}
