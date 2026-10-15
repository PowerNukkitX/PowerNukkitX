package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.math.Vector3;

public class ItemGoldenApple extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(4)
            .saturation(9.6f)
            .build();

    public ItemGoldenApple() {
        super(GOLDEN_APPLE, DEFINITION);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public boolean onEaten(Player player) {
        player.addEffect(Effect.get(EffectType.REGENERATION).setAmplifier(1).setDuration(5 * 20));
        player.addEffect(Effect.get(EffectType.ABSORPTION).setAmplifier(0).setDuration(120 * 20));
        return super.onEaten(player);
    }
}