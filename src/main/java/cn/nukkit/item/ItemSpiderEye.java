package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSpiderEye extends ItemFood {

    public ItemSpiderEye() {
        this(0, 1);
    }

    public ItemSpiderEye(Integer meta) {
        this(meta, 1);
    }

    public ItemSpiderEye(Integer meta, int count) {
        super(SPIDER_EYE, meta, count, "Spider Eye");
    }

    @Override
    public int getNutrition() {
        return 2;
    }

    @Override
    public float getSaturation() {
        return 3.2F;
    }

    @Override
    public boolean onEaten(Player player) {
        player.addEffect(Effect.get(EffectType.POISON).setDuration(5 * 20));

        return true;
    }
}
