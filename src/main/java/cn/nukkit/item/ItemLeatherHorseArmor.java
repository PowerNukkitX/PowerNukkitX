package cn.nukkit.item;

public class ItemLeatherHorseArmor extends Item {
    public ItemLeatherHorseArmor() {
        super(LEATHER_HORSE_ARMOR);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}