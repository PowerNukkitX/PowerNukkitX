package cn.nukkit.item;

public class ItemGoldenBoots extends ItemArmor {
    public ItemGoldenBoots() {
        this(0, 1);
    }

    public ItemGoldenBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenBoots(Integer meta, int count) {
        super(GOLDEN_BOOTS, meta, count, "Golden Boots");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_GOLD;
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
        return 92;
    }
}