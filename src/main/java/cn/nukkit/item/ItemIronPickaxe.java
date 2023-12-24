package cn.nukkit.item;

public class ItemIronPickaxe extends ItemTool {
    public ItemIronPickaxe() {
        this(0, 1);
    }

    public ItemIronPickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemIronPickaxe(Integer meta, int count) {
        super(IRON_PICKAXE, meta, count, "Iron Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}