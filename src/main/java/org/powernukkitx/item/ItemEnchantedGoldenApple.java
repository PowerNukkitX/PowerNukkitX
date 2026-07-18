package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.math.Vector3;

public class ItemEnchantedGoldenApple extends ItemFood {
    public ItemEnchantedGoldenApple() {
        super(ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public int getNutrition() {
        return 4;
    }

    @Override
    public float getSaturation() {
        return 2.4F;
    }

    @Override
    public boolean onEaten(Player player) {
        player.addEffect(Effect.get(EffectType.ABSORPTION)
                .setAmplifier(3)
                .setDuration(120 * 20));
        player.addEffect(Effect.get(EffectType.REGENERATION).
                setAmplifier(4).
                setDuration(30 * 20));
        player.addEffect(Effect.get(EffectType.FIRE_RESISTANCE)
                .setDuration(5 * 60 * 20));
        player.addEffect(Effect.get(EffectType.RESISTANCE)
                .setDuration(5 * 60 * 20));

        return super.onEaten(player);
    }
}