package cn.nukkit.item.tools.copper;

import cn.nukkit.item.ItemTool;

public class ItemCopperShovel extends ItemTool {
    public ItemCopperShovel() {
        super(COPPER_SHOVEL);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return WEARABLE_TIER_COPPER;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}