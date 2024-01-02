package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Potion;

public class ItemLingeringPotion extends ProjectileItem {

    public ItemLingeringPotion() {
        this(0, 1);
    }

    public ItemLingeringPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemLingeringPotion(Integer meta, int count) {
        super(LINGERING_POTION, meta, count, "Lingering Potion");
        updateName();
    }

    @Override
    public void setAux(Integer aux) {
        super.setAux(aux);
        updateName();
    }

    private void updateName() {
        int potionId = getAux();
        if (potionId == Potion.WATER) {
            name = "Lingering Water Bottle";
        } else {
            name = ItemPotion.buildName(potionId, "Lingering Potion", true);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public String getProjectileEntityType() {
        return "LingeringPotion";
    }

    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.aux);
    }
}