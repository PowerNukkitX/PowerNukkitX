package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemNetheriteSpear extends ItemSpear {

    public ItemNetheriteSpear() {
        this(0, 1);
    }

    public ItemNetheriteSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteSpear(Integer meta, int count) {
        super(NETHERITE_SPEAR, meta, count, "Netherite Spear");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }
}