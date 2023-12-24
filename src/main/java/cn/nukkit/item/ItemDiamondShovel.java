package cn.nukkit.item;

public class ItemDiamondShovel extends ItemTool {
    public ItemDiamondShovel() {
        super(DIAMOND_SHOVEL);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}