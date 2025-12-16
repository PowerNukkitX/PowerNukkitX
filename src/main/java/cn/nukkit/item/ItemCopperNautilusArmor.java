package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemCopperNautilusArmor extends ItemNautilusArmor {

    public ItemCopperNautilusArmor() {
        this(0, 1);
    }

    public ItemCopperNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemCopperNautilusArmor(Integer meta, int count) {
        super(COPPER_NAUTILUS_ARMOR, meta, count, "Copper Nautilus Armor");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_COPPER;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_COPPER;
    }

}