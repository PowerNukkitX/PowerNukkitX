package org.powernukkitx.item;

public class ItemDiamondHoe extends ItemTool {
    public ItemDiamondHoe() {
        super(DIAMOND_HOE);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }
}
