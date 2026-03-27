package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;

import java.awt.*;

public class EffectHealthBoost extends Effect {

    public EffectHealthBoost() {
        super(EffectType.HEALTH_BOOST, "%potion.healthBoost", new Color(248, 125, 35));
    }

    public void add(Entity entity) {
        entity.setHealthMax(entity.getHealthMax() + 4 * this.getLevel());
    }

    public void remove(Entity entity) {
        entity.setHealthMax(entity.getHealthMax() - 4 * this.getLevel());
        if (entity.getHealthCurrent() > entity.getHealthMax()){
            entity.setHealthCurrent(entity.getHealthMax());
        }
    }
}
