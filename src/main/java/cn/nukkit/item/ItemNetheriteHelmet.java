package cn.nukkit.item;

public class ItemNetheriteHelmet extends ItemArmor {
    public ItemNetheriteHelmet() {
        this(0, 1);
    }

    public ItemNetheriteHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteHelmet(Integer meta, int count) {
        super(NETHERITE_HELMET, meta, count, "Netherite Helmet");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_NETHERITE;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 407;
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