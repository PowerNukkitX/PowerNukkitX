package cn.nukkit.item;

public class ItemStoneShovel extends ItemTool {

    public ItemStoneShovel() {
        this(0, 1);
    }

    public ItemStoneShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneShovel(Integer meta, int count) {
        super(STONE_SHOVEL, meta, count, "Stone Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}