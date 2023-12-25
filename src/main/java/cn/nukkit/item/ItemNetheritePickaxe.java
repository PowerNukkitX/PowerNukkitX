package cn.nukkit.item;

public class ItemNetheritePickaxe extends ItemTool {
    public ItemNetheritePickaxe() {
        this(0, 1);
    }

    public ItemNetheritePickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheritePickaxe(Integer meta, int count) {
        super(NETHERITE_PICKAXE, meta, count, "Netherite Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}