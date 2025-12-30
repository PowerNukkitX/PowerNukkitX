package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.math.Vector3;

/**
 * @author joserobjr
 */
public class ItemHoneyBottle extends ItemFood {
    
    public ItemHoneyBottle() {
        this(0, 1);
    }
    
    public ItemHoneyBottle(Integer meta) {
        this(meta, 1);
    }
    
    public ItemHoneyBottle(Integer meta, int count) {
        super(HONEY_BOTTLE, meta, count, "Honey Bottle");
    }
    
    @Override
    public int getMaxStackSize() {
        return 16;
    }
    
    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public int getNutrition() {
        return 6;
    }

    @Override
    public float getSaturation() {
        return 1.2F;
    }

    @Override
    public boolean onEaten(Player player) {
        player.getInventory().addItem(new ItemGlassBottle());
        player.removeEffect(EffectType.POISON);

        return true;
    }
}
