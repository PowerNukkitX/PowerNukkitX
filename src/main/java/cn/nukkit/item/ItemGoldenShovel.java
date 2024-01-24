package cn.nukkit.item;

public class ItemGoldenShovel extends ItemTool {
    public ItemGoldenShovel() {
        this(0, 1);
    }

    public ItemGoldenShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenShovel(Integer meta, int count) {
        super(GOLDEN_SHOVEL, meta, count, "Golden Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }
}