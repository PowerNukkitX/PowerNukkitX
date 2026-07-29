package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSpiderEye extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(2)
            .saturation(3.2f)
            .build();

    public ItemSpiderEye() {
        this(0, 1);
    }

    public ItemSpiderEye(Integer meta) {
        this(meta, 1);
    }

    public ItemSpiderEye(Integer meta, int count) {
        super(SPIDER_EYE, meta, count, "Spider Eye", DEFINITION);
    }

    @Override
    public boolean onEaten(Player player) {
        player.addEffect(Effect.get(EffectType.POISON).setDuration(5 * 20));

        return super.onEaten(player);
    }
}
