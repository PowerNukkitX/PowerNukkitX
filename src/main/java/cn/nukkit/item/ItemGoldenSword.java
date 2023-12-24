package cn.nukkit.item;

public class ItemGoldenSword extends ItemTool {
    public ItemGoldenSword() {
        this(0, 1);
    }

    public ItemGoldenSword(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenSword(Integer meta, int count) {
        super(GOLDEN_SWORD, meta, count, "Golden Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}