package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemIronSpear extends ItemSpear {

    public ItemIronSpear() {
        this(0, 1);
    }

    public ItemIronSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemIronSpear(Integer meta, int count) {
        super(IRON_SPEAR, meta, count, "Iron Spear");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}