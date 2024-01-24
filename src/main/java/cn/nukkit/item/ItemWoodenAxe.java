package cn.nukkit.item;

public class ItemWoodenAxe extends ItemTool {
    public ItemWoodenAxe() {
        this(0, 1);
    }

    public ItemWoodenAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenAxe(Integer meta, int count) {
        super(WOODEN_AXE, meta, count, "Wooden Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }

    @Override
    public boolean canBreakShield() {
        return true;
    }
}