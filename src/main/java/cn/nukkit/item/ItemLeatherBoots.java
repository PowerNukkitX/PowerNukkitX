package cn.nukkit.item;

public class ItemLeatherBoots extends ItemColorArmor {
    public ItemLeatherBoots() {
        this(0, 1);
    }

    public ItemLeatherBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherBoots(Integer meta, int count) {
        super(LEATHER_BOOTS, meta, count, "Leather Boots");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_LEATHER;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 66;
    }
}