package cn.nukkit.item;

public class ItemWoodenHoe extends ItemTool {

    public ItemWoodenHoe() {
        this(0, 1);
    }

    public ItemWoodenHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenHoe(Integer meta, int count) {
        super(WOODEN_HOE, meta, count, "Wooden Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }
}