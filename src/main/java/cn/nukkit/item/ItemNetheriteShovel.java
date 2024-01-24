package cn.nukkit.item;

public class ItemNetheriteShovel extends ItemTool {
    public ItemNetheriteShovel() {
        this(0, 1);
    }

    public ItemNetheriteShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteShovel(Integer meta, int count) {
        super(NETHERITE_SHOVEL, meta, count, "Netherite Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}