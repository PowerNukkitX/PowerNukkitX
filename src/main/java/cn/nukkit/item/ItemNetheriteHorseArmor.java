package cn.nukkit.item;

public class ItemNetheriteHorseArmor extends Item {
    public ItemNetheriteHorseArmor() {
        super(NETHERITE_HORSE_ARMOR);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}