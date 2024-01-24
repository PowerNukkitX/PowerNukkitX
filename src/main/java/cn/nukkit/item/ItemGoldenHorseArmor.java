package cn.nukkit.item;

public class ItemGoldenHorseArmor extends Item {
    public ItemGoldenHorseArmor() {
        super(GOLDEN_HORSE_ARMOR);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}