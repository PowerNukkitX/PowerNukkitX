package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemWoodenSpear extends ItemSpear {

    public ItemWoodenSpear() {
        this(0, 1);
    }

    public ItemWoodenSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenSpear(Integer meta, int count) {
        super(WOODEN_SPEAR, meta, count, "Wooden Spear");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}