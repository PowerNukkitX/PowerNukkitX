package cn.nukkit.item;

public class ItemDiamondPickaxe extends ItemTool {
    public ItemDiamondPickaxe() {
        super(DIAMOND_PICKAXE);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }
}