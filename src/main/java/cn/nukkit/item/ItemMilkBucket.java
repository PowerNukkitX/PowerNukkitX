package cn.nukkit.item;

import cn.nukkit.Player;

public class ItemMilkBucket extends ItemFood {
    public ItemMilkBucket() {
        super(MILK_BUCKET);
    }

    @Override
    public void setDamage(int meta) {

    }

    @Override
    public boolean onEaten(Player player) {
        player.getInventory().addItem(Item.get(ItemID.BUCKET, 0, 1));
        player.removeAllEffects();

        return true;
    }

    @Override
    public boolean isRequiresHunger() {
        return false;
    }
}