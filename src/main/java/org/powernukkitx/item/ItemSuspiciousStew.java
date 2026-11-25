package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.item.definition.ItemDefinition;

import org.jetbrains.annotations.Nullable;

public class ItemSuspiciousStew extends ItemFood {
    /**
     * The highest aux value a stew has; every value from 0 up to it gives an effect.
     */
    public static final int MAX_META = 12;

    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .maxStackSize(1)
            .nutrition(6)
            .saturation(7.2f)
            .build();

    public ItemSuspiciousStew() {
        this(0, 1);
    }

    public ItemSuspiciousStew(Integer meta) {
        this(meta, 1);
    }

    public ItemSuspiciousStew(Integer meta, int count) {
        super(SUSPICIOUS_STEW, meta, count, "Suspicious Stew", DEFINITION);
    }

    @Override
    public boolean canAlwaysEat() {
        return true;
    }

    @Override
    public boolean onEaten(Player player) {
        Effect effect = getEffect(meta);

        if(effect != null) {
            player.addEffect(effect);
        }

        return super.onEaten(player);
    }

    /**
     * @param meta the stew's aux value, which records the flower it was made with
     * @return a new instance of the effect eating that stew gives, or null for an unknown value
     */
    public static @Nullable Effect getEffect(int meta) {
        return switch(meta) {
            case 0, 10 -> Effect.get(EffectType.NIGHT_VISION).setDuration(5 * 20);
            case 1 -> Effect.get(EffectType.JUMP_BOOST).setDuration(5 * 20);
            case 2 -> Effect.get(EffectType.WEAKNESS).setDuration(7 * 20);
            case 3, 11 -> Effect.get(EffectType.BLINDNESS).setDuration(7 * 20);
            case 4 -> Effect.get(EffectType.POISON).setDuration(11 * 20);
            case 5, 6 -> Effect.get(EffectType.SATURATION).setDuration(6);
            case 7 -> Effect.get(EffectType.FIRE_RESISTANCE).setDuration(3 * 20);
            case 8 -> Effect.get(EffectType.REGENERATION).setDuration(7 * 20);
            case 9 -> Effect.get(EffectType.WITHER).setDuration(7 * 20);
            case 12 -> Effect.get(EffectType.NAUSEA).setDuration(7 * 20);
            default -> null;
        };
    }

    /**
     * @param effectId the numeric id of an effect
     * @return the lowest aux value whose stew gives that effect, or -1 when no stew gives it
     */
    public static int getMeta(int effectId) {
        for (int meta = 0; meta <= MAX_META; meta++) {
            Effect effect = getEffect(meta);
            if (effect != null && effect.getId() == effectId) {
                return meta;
            }
        }
        return -1;
    }
}
