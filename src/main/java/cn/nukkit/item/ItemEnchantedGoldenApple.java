package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.math.Vector3;

public class ItemEnchantedGoldenApple extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemEnchantedGoldenApple() {
        super(ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 2.4F;
    }

    @Override
    /**
     * @deprecated 
     */
    
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

        return true;
    }
}