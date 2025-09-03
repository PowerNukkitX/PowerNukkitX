package cn.nukkit.item;

public class ItemIronChestplate extends ItemArmor {

    public ItemIronChestplate() {
        this(0, 1);
    }

    public ItemIronChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemIronChestplate(Integer meta, int count) {
        super(IRON_CHESTPLATE, meta, count, "Iron Chestplate");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_IRON;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 6;
    }

    @Override
    public int getMaxDurability() {
        return 241;
    }
}