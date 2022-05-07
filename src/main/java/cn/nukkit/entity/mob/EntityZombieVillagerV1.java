package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

/**
 * @author PikyCZ
 */
public class EntityZombieVillagerV1 extends EntityMob implements EntitySmite {

    public static final int NETWORK_ID = 44;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityZombieVillagerV1(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Zombie Villager";
    }

    @PowerNukkitOnly
    @Override
    public boolean isUndead() {
        return true;
    }
    @PowerNukkitXOnly
    @Override
    public boolean onUpdate(int currentTick) {
        if (this.getLevel().getDimension() == Level.DIMENSION_OVERWORLD)
            if (this.getLevel().getTime() > 0 && this.getLevel().getTime() <= 12000)
                if (!this.hasEffect(Effect.FIRE_RESISTANCE))
                    if (!this.isInsideOfWater())
                        if (!this.isUnderBlock())
                            if (!this.isOnFire())
                                this.setOnFire(1);
        return super.onUpdate(currentTick);
    }
}
