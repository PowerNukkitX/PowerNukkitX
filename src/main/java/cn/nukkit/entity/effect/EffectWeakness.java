package cn.nukkit.entity.effect;

import java.awt.*;

public class EffectWeakness extends Effect {

    public EffectWeakness() {
        super(EffectType.WEAKNESS, "%potion.weakness", new Color(72, 77, 72), true);
    }
}
