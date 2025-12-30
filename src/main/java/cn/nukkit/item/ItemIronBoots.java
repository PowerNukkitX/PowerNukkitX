package cn.nukkit.item;

public class ItemIronBoots extends ItemArmor {
    public ItemIronBoots() {
        this(0, 1);
    }

    public ItemIronBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemIronBoots(Integer meta, int count) {
        super(IRON_BOOTS, meta, count, "Iron Boots");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_IRON;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 196;
    }
}