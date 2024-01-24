package cn.nukkit.item;

public class ItemDiamondAxe extends ItemTool {
    public ItemDiamondAxe() {
        super(DIAMOND_AXE);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

    @Override
    public boolean canBreakShield() {
        return true;
    }
}