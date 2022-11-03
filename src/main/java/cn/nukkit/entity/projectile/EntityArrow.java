package cn.nukkit.entity.projectile;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import co.aikar.timings.Timings;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityArrow extends EntityProjectile {

    public static final int NETWORK_ID = 80;

    protected int pickupMode;

    public EntityArrow(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.setCritical(critical);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.05f;
    }

    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.1f;
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
    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }
        Timings.entityMoveTimer.startTiming();

        this.ySize *= 0.4;

        double movX = dx;
        double movY = dy;
        double movZ = dz;

        var currentAABB = this.boundingBox.clone();
        var dirvec = new Vector3(dx, dy, dz).multiply(1 / (double) 10);
        boolean isCollision = false;
        for (int i = 0; i < 10; ++i) {
            var collisionResult = this.level.fastCollisionCubes(this, currentAABB.offset(dirvec.x, dirvec.y, dirvec.z), false);
            if (!collisionResult.isEmpty()) {
                isCollision = true;
                break;
            }
        }
        if (isCollision) {
            if (dy > 0 && this.boundingBox.getMaxY() <= currentAABB.getMinY()) {
                double y1 = currentAABB.getMinY() - this.boundingBox.getMaxY();
                if (y1 < dy) {
                    dy = y1;
                }
            }
            if (dy < 0 && this.boundingBox.getMinY() >= currentAABB.getMaxY()) {
                double y2 = currentAABB.getMaxY() - this.boundingBox.getMinY();
                if (y2 > dy) {
                    dy = y2;
                }
            }

            if (dx > 0 && this.boundingBox.getMaxX() <= currentAABB.getMinX()) {
                double x1 = currentAABB.getMinX() - this.boundingBox.getMaxX();
                if (x1 < dx) {
                    dx = x1;
                }
            }
            if (dx < 0 && this.boundingBox.getMinX() >= currentAABB.getMaxX()) {
                double x2 = currentAABB.getMaxX() - this.boundingBox.getMinX();
                if (x2 > dx) {
                    dx = x2;
                }
            }

            if (dz > 0 && this.boundingBox.getMaxZ() <= currentAABB.getMinZ()) {
                double z1 = currentAABB.getMinZ() - this.boundingBox.getMaxZ();
                if (z1 < dz) {
                    dz = z1;
                }
            }
            if (dz < 0 && this.boundingBox.getMinZ() >= currentAABB.getMaxZ()) {
                double z2 = currentAABB.getMaxZ() - this.boundingBox.getMinZ();
                if (z2 > dz) {
                    dz = z2;
                }
            }
        }
        this.boundingBox.offset(0, dy, 0);
        this.boundingBox.offset(dx, 0, 0);
        this.boundingBox.offset(0, 0, dz);
        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();

        this.checkGroundState(movX, movY, movZ, dx, dy, dz);
        this.updateFallState(this.onGround);

        if (movX != dx) {
            this.motionX = 0;
        }

        if (movY != dy) {
            this.motionY = 0;
        }

        if (movZ != dz) {
            this.motionZ = 0;
        }

        //TODO: vehicle collision events (first we need to spawn them!)
        Timings.entityMoveTimer.stopTiming();
        return true;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
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
        closeOnCollide = false;

        this.damage = namedTag.contains("damage") ? namedTag.getDouble("damage") : 2;
        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public boolean isCritical() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL);
    }

    public void setCritical(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL, value);
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

        this.timing.startTiming();

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

        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return !hadCollision;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    protected void afterCollisionWithEntity(Entity entity) {
        if (hadCollision) {
            close();
        } else {
            setMotion(getMotion().divide(-4));
        }
    }

    @PowerNukkitOnly
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

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Arrow";
    }
}
