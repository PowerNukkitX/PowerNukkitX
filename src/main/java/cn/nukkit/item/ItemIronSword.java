package cn.nukkit.item;

public class ItemIronSword extends ItemTool {
    public ItemIronSword() {
        this(0, 1);
    }

    public ItemIronSword(Integer meta) {
        this(meta, 1);
    }

    public ItemIronSword(Integer meta, int count) {
        super(IRON_SWORD, meta, count, "Iron Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }
}