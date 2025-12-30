package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemGoldenNautilusArmor extends ItemNautilusArmor {

    public ItemGoldenNautilusArmor() {
        this(0, 1);
    }

    public ItemGoldenNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenNautilusArmor(Integer meta, int count) {
        super(GOLDEN_NAUTILUS_ARMOR, meta, count, "Golden Nautilus Armor");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

}