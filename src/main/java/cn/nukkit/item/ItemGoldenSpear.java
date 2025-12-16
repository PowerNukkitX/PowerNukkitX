package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemGoldenSpear extends ItemSpear {

    public ItemGoldenSpear() {
        this(0, 1);
    }

    public ItemGoldenSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenSpear(Integer meta, int count) {
        super(GOLDEN_SPEAR, meta, count, "Golden Spear");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}