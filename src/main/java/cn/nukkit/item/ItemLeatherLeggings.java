package cn.nukkit.item;

public class ItemLeatherLeggings extends ItemColorArmor {
    public ItemLeatherLeggings() {
        this(0, 1);
    }

    public ItemLeatherLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherLeggings(Integer meta, int count) {
        super(LEATHER_LEGGINGS, meta, count);
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_LEATHER;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 76;
    }
}