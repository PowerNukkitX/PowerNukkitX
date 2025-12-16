package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemDiamondNautilusArmor extends ItemNautilusArmor {

    public ItemDiamondNautilusArmor() {
        this(0, 1);
    }

    public ItemDiamondNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemDiamondNautilusArmor(Integer meta, int count) {
        super(DIAMOND_NAUTILUS_ARMOR, meta, count, "Diamond Nautilus Armor");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

}