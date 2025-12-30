package cn.nukkit.item;

public class ItemLeatherHelmet extends ItemColorArmor {
    public ItemLeatherHelmet() {
        this(0, 1);
    }

    public ItemLeatherHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherHelmet(Integer meta, int count) {
        super(LEATHER_HELMET, meta, count);
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_LEATHER;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 56;
    }
}