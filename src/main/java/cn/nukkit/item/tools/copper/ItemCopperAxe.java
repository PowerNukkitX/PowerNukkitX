package cn.nukkit.item.tools.copper;

import cn.nukkit.item.ItemTool;

public class ItemCopperAxe extends ItemTool {
    public ItemCopperAxe() {
        super(COPPER_AXE);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public boolean isAxe() {
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

    @Override
    public boolean canBreakShield() {
        return true;
    }
}