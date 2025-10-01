package cn.nukkit.item;

public class ItemLeatherChestplate extends ItemColorArmor {
    public ItemLeatherChestplate() {
        this(0, 1);
    }

    public ItemLeatherChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherChestplate(Integer meta, int count) {
        super(LEATHER_CHESTPLATE, meta, count);
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_LEATHER;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 81;
    }
}