package cn.nukkit.item;

public class ItemGoldenPickaxe extends ItemTool {
    public ItemGoldenPickaxe() {
        this(0, 1);
    }

    public ItemGoldenPickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenPickaxe(Integer meta, int count) {
        super(GOLDEN_PICKAXE, meta, count, "Golden Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}