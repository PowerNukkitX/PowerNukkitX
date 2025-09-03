package cn.nukkit.item;

public class ItemNetheriteChestplate extends ItemArmor {
    public ItemNetheriteChestplate() {
        this(0, 1);
    }

    public ItemNetheriteChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteChestplate(Integer meta, int count) {
        super(NETHERITE_CHESTPLATE, meta, count, "Netherite Chestplate");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_NETHERITE;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 8;
    }

    @Override
    public int getMaxDurability() {
        return 592;
    }

    @Override
    public int getToughness() {
        return 3;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}