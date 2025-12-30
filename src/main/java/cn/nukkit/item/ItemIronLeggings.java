package cn.nukkit.item;

public class ItemIronLeggings extends ItemArmor {
    public ItemIronLeggings() {
        this(0, 1);
    }

    public ItemIronLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemIronLeggings(Integer meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_IRON;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 5;
    }

    @Override
    public int getMaxDurability() {
        return 226;
    }
}