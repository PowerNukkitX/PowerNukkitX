package cn.nukkit.entity.effect;

import java.awt.*;

public class EffectDarkness extends Effect {

    public EffectDarkness() {
        super(EffectType.DARKNESS, "%effect.darkness", new Color(41, 39, 33), true);
        this.setVisible(false);
    }
}
