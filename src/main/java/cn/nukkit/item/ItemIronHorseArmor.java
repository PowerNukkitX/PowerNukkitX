package cn.nukkit.item;

public class ItemIronHorseArmor extends Item {
    public ItemIronHorseArmor() {
        super(IRON_HORSE_ARMOR);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}