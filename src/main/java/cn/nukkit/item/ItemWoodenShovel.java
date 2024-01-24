package cn.nukkit.item;

public class ItemWoodenShovel extends ItemTool {

    public ItemWoodenShovel() {
        this(0, 1);
    }

    public ItemWoodenShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenShovel(Integer meta, int count) {
        super(WOODEN_SHOVEL, meta, count, "Wooden Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }
}