package cn.nukkit.entity.effect;

import java.awt.*;

public class EffectVillageHero extends Effect {

    public EffectVillageHero() {
        super(EffectType.VILLAGE_HERO, "%effect.villageHero", new Color(68, 255, 68));
        this.setVisible(false);
    }
}
