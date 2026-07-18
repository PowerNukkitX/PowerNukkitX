package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRottenFlesh extends ItemFood {

    public ItemRottenFlesh() {
        this(0, 1);
    }

    public ItemRottenFlesh(Integer meta) {
        this(meta, 1);
    }

    public ItemRottenFlesh(Integer meta, int count) {
        super(ROTTEN_FLESH, meta, count, "Rotten Flesh");
    }

    @Override
    public int getNutrition() {
        return 4;
    }

    @Override
    public float getSaturation() {
        return 0.8F;
    }

    @Override
    public boolean onEaten(Player player) {
        if(0.8F >= Math.random()) {
            player.addEffect(Effect.get(EffectType.HUNGER).setDuration(30 * 20));
        }

        return super.onEaten(player);
    }
}
