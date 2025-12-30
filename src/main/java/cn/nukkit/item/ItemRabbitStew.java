package cn.nukkit.item;

import cn.nukkit.Player;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRabbitStew extends ItemFood {

    public ItemRabbitStew() {
        this(0, 1);
    }

    public ItemRabbitStew(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitStew(Integer meta, int count) {
        super(RABBIT_STEW, meta, count, "Rabbit Stew");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getNutrition() {
        return 10;
    }

    @Override
    public float getSaturation() {
        return 12F;
    }

    @Override
    public boolean onEaten(Player player) {
        player.getInventory().addItem(new ItemBowl());

        return true;
    }
}
