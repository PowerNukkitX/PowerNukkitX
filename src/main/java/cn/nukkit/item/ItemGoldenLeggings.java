package cn.nukkit.item;

public class ItemGoldenLeggings extends ItemArmor {
    public ItemGoldenLeggings() {
        this(0, 1);
    }

    public ItemGoldenLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenLeggings(Integer meta, int count) {
        super(GOLDEN_LEGGINGS, meta, count, "Golden Leggings");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_GOLD;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 106;
    }
}