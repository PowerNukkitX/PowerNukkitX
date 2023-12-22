package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Potion;

/**
 * @author xtypr
 * @since 2015/12/27
 */
public class ItemPotionSplash extends ProjectileItem {


    public ItemPotionSplash() {
        this(0, 1);
    }

    public ItemPotionSplash(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionSplash(Integer meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion");
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
            name = "Splash Water Bottle";
        } else {
            name = ItemPotion.buildName(potionId, "Splash Potion", true);
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
        return "ThrownPotion";
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
