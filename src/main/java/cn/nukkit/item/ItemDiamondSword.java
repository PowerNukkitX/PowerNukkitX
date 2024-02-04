package cn.nukkit.item;

public class ItemDiamondSword extends ItemTool {
    public ItemDiamondSword() {
        super(DIAMOND_SWORD);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 7;
    }
}