package cn.nukkit.item;

public class ItemIronHoe extends ItemTool {
    public ItemIronHoe() {
        this(0, 1);
    }

    public ItemIronHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemIronHoe(Integer meta, int count) {
        super(IRON_HOE, meta, count, "Iron Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }
}