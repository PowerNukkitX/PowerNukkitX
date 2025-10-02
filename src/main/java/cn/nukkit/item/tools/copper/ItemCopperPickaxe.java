package cn.nukkit.item.tools.copper;

import cn.nukkit.item.ItemTool;

public class ItemCopperPickaxe extends ItemTool {
    public ItemCopperPickaxe() {
        super(COPPER_PICKAXE);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public boolean isPickaxe() {
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