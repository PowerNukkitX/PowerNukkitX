package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.entity.effect.PotionType;

public class ItemSplashPotion extends ProjectileItem {
    /**
     * @deprecated 
     */
    
    public ItemSplashPotion() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSplashPotion(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSplashPotion(Integer meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion");
        updateName();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    
    /**
     * @deprecated 
     */
    private void updateName() {
        PotionType $1 = PotionType.get(getDamage());
        if (PotionType.WATER.equals(potion)) {
            name = "Splash Water Bottle";
        } else {
            name = ItemPotion.buildName(potion, "Splash Potion", true);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getProjectileEntityType() {
        return SPLASH_POTION;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.meta);
    }
}