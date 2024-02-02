package cn.nukkit.item;

public class ItemBoneMeal extends Item {
    public ItemBoneMeal() {
        super(BONE_MEAL);
    }

    @Override
    public boolean isFertilizer() {
        return true;
    }
}