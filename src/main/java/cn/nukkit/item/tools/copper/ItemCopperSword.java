package cn.nukkit.item.tools.copper;

import cn.nukkit.item.ItemTool;

public class ItemCopperSword extends ItemTool {
    public ItemCopperSword() {
        super(COPPER_SWORD);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public boolean isSword() {
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