package cn.nukkit.item;

public class ItemNetheriteSword extends ItemTool {
    public ItemNetheriteSword() {
        this(0, 1);
    }

    public ItemNetheriteSword(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteSword(Integer meta, int count) {
        super(NETHERITE_SWORD, meta, count, "Netherite Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isSword() {
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
}