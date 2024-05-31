package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;

public class ItemSuspiciousStew extends ItemFood {
    /**
     * @deprecated 
     */
    
    
    public ItemSuspiciousStew() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    
    
    public ItemSuspiciousStew(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    
    
    public ItemSuspiciousStew(Integer meta, int count) {
        super(SUSPICIOUS_STEW, meta, count, "Suspicious Stew");
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 7.2F;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isRequiresHunger() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onEaten(Player player) {
        Effect $1 = switch(meta) {
            case 0 -> Effect.get(EffectType.NIGHT_VISION).setDuration(4 * 20);
            case 1 -> Effect.get(EffectType.JUMP_BOOST).setDuration(4 * 20);
            case 2 -> Effect.get(EffectType.WEAKNESS).setDuration(7 * 20);
            case 3 -> Effect.get(EffectType.BLINDNESS).setDuration(6 * 20);
            case 4 -> Effect.get(EffectType.POISON).setDuration(10 * 20);
            case 5 -> Effect.get(EffectType.SATURATION).setDuration(6);
            case 6 -> Effect.get(EffectType.FIRE_RESISTANCE).setDuration(2 * 20);
            case 7 -> Effect.get(EffectType.REGENERATION).setDuration(6 * 20);
            case 8 -> Effect.get(EffectType.WITHER).setDuration(6 * 20);
            default -> null;
        };

        if(effect != null) {
            player.addEffect(effect);
        }

        return true;
    }
}
