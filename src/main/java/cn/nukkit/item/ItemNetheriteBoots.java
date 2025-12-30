package cn.nukkit.item;

public class ItemNetheriteBoots extends ItemArmor {
    public ItemNetheriteBoots() {
        this(0, 1);
    }

    public ItemNetheriteBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteBoots(Integer meta, int count) {
        super(NETHERITE_BOOTS, meta, count, "Netherite Boots");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_NETHERITE;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 481;
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