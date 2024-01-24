package cn.nukkit.item;

public class ItemNetheriteAxe extends ItemTool {
    public ItemNetheriteAxe() {
        this(0, 1);
    }

    public ItemNetheriteAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteAxe(Integer meta, int count) {
        super(NETHERITE_AXE, meta, count, "Netherite Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 8;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public boolean canBreakShield() {
        return true;
    }
}