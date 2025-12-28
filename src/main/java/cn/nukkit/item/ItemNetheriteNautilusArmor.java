package cn.nukkit.item;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemNetheriteNautilusArmor extends ItemNautilusArmor {

    public ItemNetheriteNautilusArmor() {
        this(0, 1);
    }

    public ItemNetheriteNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteNautilusArmor(Integer meta, int count) {
        super(NETHERITE_NAUTILUS_ARMOR, meta, count, "Netherite Nautilus Armor");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

}