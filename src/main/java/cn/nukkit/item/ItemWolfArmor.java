package cn.nukkit.item;

public class ItemWolfArmor extends ItemColorArmor {
     public ItemWolfArmor() {
         super(WOLF_ARMOR);
     }

    @Override
    public int getMaxDurability() {
        return 64;
    }
}