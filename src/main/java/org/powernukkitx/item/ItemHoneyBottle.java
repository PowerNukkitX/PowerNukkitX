package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.math.Vector3;

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
        if (player.isAdventure() || player.isSurvival()) {
            player.getInventory().addItem(new ItemGlassBottle());
        }
        player.removeEffect(EffectType.POISON);

        return super.onEaten(player);
    }
}
