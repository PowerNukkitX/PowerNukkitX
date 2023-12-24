package cn.nukkit.item;

public class ItemDiamondHorseArmor extends Item {
    public ItemDiamondHorseArmor() {
        super(DIAMOND_HORSE_ARMOR);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}