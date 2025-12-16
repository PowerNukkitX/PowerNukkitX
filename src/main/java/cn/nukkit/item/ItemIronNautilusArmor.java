package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemIronNautilusArmor extends ItemNautilusArmor {

    public ItemIronNautilusArmor() {
        this(0, 1);
    }

    public ItemIronNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemIronNautilusArmor(Integer meta, int count) {
        super(IRON_NAUTILUS_ARMOR, meta, count, "Iron Nautilus Armor");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

}