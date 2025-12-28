package cn.nukkit.item;

public class ItemNetheriteLeggings extends ItemArmor {

    public ItemNetheriteLeggings() {
        this(0, 1);
    }

    public ItemNetheriteLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteLeggings(Integer meta, int count) {
        super(NETHERITE_LEGGINGS, meta, count, "Netherite Leggings");
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_NETHERITE;
    }

    @Override
    public int getArmorPoints() {
        return 6;
    }

    @Override
    public int getMaxDurability() {
        return 555;
    }

    @Override
    public int getToughness() {
        return 3;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}