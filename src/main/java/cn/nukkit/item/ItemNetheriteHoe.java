package cn.nukkit.item;

public class ItemNetheriteHoe extends ItemTool {

    public ItemNetheriteHoe() {
        this(0, 1);
    }

    public ItemNetheriteHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteHoe(Integer meta, int count) {
        super(NETHERITE_HOE, meta, count, "Netherite Hoe");
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}