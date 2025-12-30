package cn.nukkit.item;

public class ItemIronHelmet extends ItemArmor {
    public ItemIronHelmet() {
        this(0, 1);
    }

    public ItemIronHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemIronHelmet(Integer meta, int count) {
        super(IRON_HELMET, meta, count, "Iron Helmet");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_IRON;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }
}