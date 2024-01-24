package cn.nukkit.item;

public class ItemStoneSword extends ItemTool {

    public ItemStoneSword() {
        this(0, 1);
    }

    public ItemStoneSword(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneSword(Integer meta, int count) {
        super(STONE_SWORD, meta, count, "Stone Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }
}