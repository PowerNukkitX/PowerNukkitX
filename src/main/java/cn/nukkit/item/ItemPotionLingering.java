package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Potion;

public class ItemPotionLingering extends ProjectileItem {

    public ItemPotionLingering() {
        this(0, 1);
    }

    public ItemPotionLingering(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionLingering(Integer meta, int count) {
        super(LINGERING_POTION, meta, count, "Lingering Potion");
        updateName();
    }

    @Override
    public void setDamage(Integer meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        int potionId = getDamage();
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

    @PowerNukkitOnly

    @Override
    public String getProjectileEntityType() {
        return "LingeringPotion";
    }

    @PowerNukkitOnly
    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @PowerNukkitOnly

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.meta);
    }
}
