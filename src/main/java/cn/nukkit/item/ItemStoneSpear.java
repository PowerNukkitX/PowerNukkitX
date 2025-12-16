package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemStoneSpear extends ItemSpear {

    public ItemStoneSpear() {
        this(0, 1);
    }

    public ItemStoneSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneSpear(Integer meta, int count) {
        super(STONE_SPEAR, meta, count, "Stone Spear");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }
}