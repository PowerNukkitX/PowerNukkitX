package cn.nukkit.entity.projectile;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityArrow extends SlenderProjectile {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return ARROW;
    }

    protected int pickupMode;
    /**
     * @deprecated 
     */
    

    public EntityArrow(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }
    /**
     * @deprecated 
     */
    

    public EntityArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.setCritical(critical);
    }


    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return 0.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getGravity() {
        return 0.05f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getDrag() {
        return 0.01f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void updateMotion() {
        if (!isInsideOfWater()) {
            super.updateMotion();
            return;
        }

        float $1 = 1 - this.getDrag() * 20;

        motionY -= getGravity() * 2;
        if (motionY < 0) {
            motionY *= drag / 1.5;
        }
        motionX *= drag;
        motionZ *= drag;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.initEntity();

        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;
    }
    /**
     * @deprecated 
     */
    

    public void setCritical() {
        this.setCritical(true);
    }
    /**
     * @deprecated 
     */
    

    public boolean isCritical() {
        return this.getDataFlag(EntityFlag.CRITICAL);
    }
    /**
     * @deprecated 
     */
    

    public void setCritical(boolean value) {
        this.setDataFlag(EntityFlag.CRITICAL, value);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getResultDamage() {
        int $2 = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected double getBaseDamage() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean $3 = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
        }

        if (this.age > 1200) {
            this.close();
            hasUpdate = true;
        }

        if (this.level.isRaining() && this.fireTicks > 0 && this.level.canBlockSeeSky(this)) {
            extinguish();

            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeMovedByCurrents() {
        return !hadCollision;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void afterCollisionWithEntity(Entity entity) {
        if (hadCollision) {
            close();
        } else {
            setMotion(getMotion().divide(-4));
        }
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void addHitEffect() {
        this.level.addSound(this, Sound.RANDOM_BOWHIT);
        EntityEventPacket $4 = new EntityEventPacket();
        packet.eid = getId();
        packet.event = EntityEventPacket.ARROW_SHAKE;
        packet.data = 7; // TODO Magic value. I have no idea why we have to set it to 7 here...
        Server.broadcastPacket(this.hasSpawned.values(), packet);
        onGround = true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("pickup", this.pickupMode);
    }
    /**
     * @deprecated 
     */
    

    public int getPickupMode() {
        return this.pickupMode;
    }
    /**
     * @deprecated 
     */
    

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Arrow";
    }
}
