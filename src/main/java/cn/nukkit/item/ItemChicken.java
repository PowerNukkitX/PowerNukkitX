package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;

public class ItemChicken extends ItemFood {
    public ItemChicken() {
        super(CHICKEN, 0, 1, "Raw Chicken");
    }

    public ItemChicken(int count) {
        super(CHICKEN, 0, count, "Raw Chicken");
    }

    @Override
    public int getNutrition() {
        return 2;
    }

    @Override
    public float getSaturation() {
        return 1.2F;
    }

    @Override
    public boolean onEaten(Player player) {
        if(0.3F >= Math.random()) {
            player.addEffect(Effect.get(EffectType.HUNGER).setDuration(30 * 20));
        }

        return true;
    }

}