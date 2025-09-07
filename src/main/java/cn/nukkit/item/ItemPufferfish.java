package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemPufferfish extends ItemFish {
    public ItemPufferfish() {
        this(0, 1);
    }

    public ItemPufferfish(Integer meta) {
        this(meta, 1);
    }

    public ItemPufferfish(Integer meta, int count) {
        super(PUFFERFISH, meta, count);
    }

    @Override
    public int getNutrition() {
        return 1;
    }

    @Override
    public float getSaturation() {
        return 0.2F;
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

        return true;
    }
}
