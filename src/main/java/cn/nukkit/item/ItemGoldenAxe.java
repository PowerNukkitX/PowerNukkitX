package cn.nukkit.item;

public class ItemGoldenAxe extends ItemTool {
    public ItemGoldenAxe() {
        this(0, 1);
    }

    public ItemGoldenAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenAxe(Integer meta, int count) {
        super(GOLDEN_AXE, meta, count, "Golden Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }

    @Override
    public boolean canBreakShield() {
        return true;
    }
}