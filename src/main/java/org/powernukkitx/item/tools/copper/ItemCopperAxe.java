package org.powernukkitx.item.tools.copper;

import org.powernukkitx.item.ItemTool;

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
        return 5;
    }

    @Override
    public boolean canBreakShield() {
        return true;
    }
}
