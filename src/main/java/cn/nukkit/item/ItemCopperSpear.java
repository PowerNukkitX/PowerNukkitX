package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemCopperSpear extends ItemSpear {

    public ItemCopperSpear() {
        this(0, 1);
    }

    public ItemCopperSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemCopperSpear(Integer meta, int count) {
        super(COPPER_SPEAR, meta, count, "Copper Spear");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_COPPER;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }
}