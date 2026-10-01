package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemPufferfish extends ItemFish {
    public static final ItemDefinition DEFINITION = ItemFish.DEFINITION.toBuilder()
            .nutrition(1)
            .saturation(0.2f)
            .build();

    public ItemPufferfish() {
        this(0, 1);
    }

    public ItemPufferfish(Integer meta) {
        this(meta, 1);
    }

    public ItemPufferfish(Integer meta, int count) {
        super(PUFFERFISH, meta, count, DEFINITION);
    }

    @Override
    public boolean onEaten(Player player) {
        player.addEffect(Effect.get(EffectType.HUNGER)
                .setDuration(15*20)
                .setAmplifier(2));
        player.addEffect(Effect.get(EffectType.POISON)
                .setDuration(60*20)
                .setAmplifier(1));
        player.addEffect(Effect.get(EffectType.NAUSEA)
                .setDuration(15*20)
                .setAmplifier(1));

        return super.onEaten(player);
    }
}
