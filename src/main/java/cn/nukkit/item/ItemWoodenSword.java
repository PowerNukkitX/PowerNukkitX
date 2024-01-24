package cn.nukkit.item;

public class ItemWoodenSword extends ItemTool {

    public ItemWoodenSword() {
        this(0, 1);
    }

    public ItemWoodenSword(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenSword(Integer meta, int count) {
        super(WOODEN_SWORD, meta, count, "Wooden Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}