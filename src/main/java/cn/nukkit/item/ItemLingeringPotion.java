package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.entity.effect.PotionType;

public class ItemLingeringPotion extends ProjectileItem {
    /**
     * @deprecated 
     */
    

    public ItemLingeringPotion() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLingeringPotion(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLingeringPotion(Integer meta, int count) {
        super(LINGERING_POTION, meta, count, "Lingering Potion");
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
            name = "Lingering Water Bottle";
        } else {
            name = ItemPotion.buildName(potion, "Lingering Potion", true);
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
        return LINGERING_POTION;
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