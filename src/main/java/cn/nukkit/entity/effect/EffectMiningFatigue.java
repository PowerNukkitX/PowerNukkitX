package cn.nukkit.entity.effect;

import java.awt.*;

public class EffectMiningFatigue extends Effect {

    public EffectMiningFatigue() {
        super(EffectType.MINING_FATIGUE, "%potion.digSlowDown", new Color(74, 66, 23), true);
    }
}
