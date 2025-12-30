package cn.nukkit.item.tools.copper;

import cn.nukkit.item.ItemTool;

public class ItemCopperHoe extends ItemTool {
    public ItemCopperHoe() {
        super(COPPER_HOE);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return WEARABLE_TIER_COPPER;
    }
}