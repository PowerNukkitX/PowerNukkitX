package cn.nukkit.item;

public class ItemIronShovel extends ItemTool {
    public ItemIronShovel() {
        this(0, 1);
    }

    public ItemIronShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemIronShovel(Integer meta, int count) {
        super(IRON_SHOVEL, meta, count, "Iron Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }
}