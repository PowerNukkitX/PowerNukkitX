package cn.nukkit.item;

public class ItemGoldenHoe extends ItemTool {
    public ItemGoldenHoe() {
        this(0, 1);
    }

    public ItemGoldenHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenHoe(Integer meta, int count) {
        super(GOLDEN_HOE, meta, count, "Golden Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }
}