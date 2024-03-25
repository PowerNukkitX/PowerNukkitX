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
    public String getIdentifier() {
        return ARROW;
    }

    protected int pickupMode;

    public EntityArrow(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.setCritical(critical);
    }


    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    @Override
    protected void updateMotion() {
        if (!isInsideOfWater()) {
            super.updateMotion();
            return;
        }

        float drag = 1 - this.getDrag() * 20;

        motionY -= getGravity() * 2;
        if (motionY < 0) {
            motionY *= drag / 1.5;
        }
        motionX *= drag;
        motionZ *= drag;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public boolean isCritical() {
        return this.getDataFlag(EntityFlag.CRITICAL);
    }

    public void setCritical(boolean value) {
        this.setDataFlag(EntityFlag.CRITICAL, value);
    }

    @Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 2;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

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
    public boolean canBeMovedByCurrents() {
        return !hadCollision;
    }

    @Override
    protected void afterCollisionWithEntity(Entity entity) {
        if (hadCollision) {
            close();
        } else {
            setMotion(getMotion().divide(-4));
        }
    }

    @Override
    protected void addHitEffect() {
        this.level.addSound(this, Sound.RANDOM_BOWHIT);
        EntityEventPacket packet = new EntityEventPacket();
        packet.eid = getId();
        packet.event = EntityEventPacket.ARROW_SHAKE;
        packet.data = 7; // TODO Magic value. I have no idea why we have to set it to 7 here...
        Server.broadcastPacket(this.hasSpawned.values(), packet);
        onGround = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("pickup", this.pickupMode);
    }

    public int getPickupMode() {
        return this.pickupMode;
    }

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }

    @Override
    public String getOriginalName() {
        return "Arrow";
    }
}
