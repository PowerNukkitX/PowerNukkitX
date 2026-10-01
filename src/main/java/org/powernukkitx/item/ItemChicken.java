package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemChicken extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(2)
            .saturation(1.2f)
            .build();

    public ItemChicken() {
        super(CHICKEN, 0, 1, "Raw Chicken", DEFINITION);
    }

    public ItemChicken(int count) {
        super(CHICKEN, 0, count, "Raw Chicken", DEFINITION);
    }

    @Override
    public boolean onEaten(Player player) {
        if(0.3F >= Math.random()) {
            player.addEffect(Effect.get(EffectType.HUNGER).setDuration(30 * 20));
        }

        return super.onEaten(player);
    }

}