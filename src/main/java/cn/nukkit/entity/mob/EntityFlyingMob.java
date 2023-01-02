package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 空中飞行怪物
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityFlyingMob extends EntityMob {
    public EntityFlyingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void handleGravity() {
        resetFallDistance();
    }

    @Override
    protected void handleFrictionMovement() {
        final double friction = getFrictionFactor();
        final double reduce = getMovementSpeedAtBlock(this.getTickCachedLevelBlock()) * (1 - friction * 0.85) * 0.43;
        if (Math.abs(this.motionZ) < PRECISION && Math.abs(this.motionX) < PRECISION && Math.abs(this.motionY) < PRECISION) {
            return;
        }
        final double angle = StrictMath.atan2(this.motionZ, this.motionX);
        double tmp = (StrictMath.cos(angle) * reduce);
        if (this.motionX > PRECISION) {
            this.motionX -= tmp;
            if (this.motionX < PRECISION) {
                this.motionX = 0;
            }
        } else if (this.motionX < -PRECISION) {
            this.motionX -= tmp;
            if (this.motionX > -PRECISION) {
                this.motionX = 0;
            }
        } else {
            this.motionX = 0;
        }
        //motionY
        tmp = reduce * friction;
        if (this.motionY > PRECISION) {
            this.motionY -= tmp;
            if (this.motionY < PRECISION) {
                this.motionY = 0;
            }
        } else if (this.motionY < -PRECISION) {
            this.motionY += tmp;
            if (this.motionY > -PRECISION) {
                this.motionY = 0;
            }
        } else {
            this.motionY = 0;
        }
        tmp = (StrictMath.sin(angle) * reduce);
        if (this.motionZ > PRECISION) {
            this.motionZ -= tmp;
            if (this.motionZ < PRECISION) {
                this.motionZ = 0;
            }
        } else if (this.motionZ < -PRECISION) {
            this.motionZ -= tmp;
            if (this.motionZ > -PRECISION) {
                this.motionZ = 0;
            }
        } else {
            this.motionZ = 0;
        }
    }

    @Override
    public boolean isFalling() {
        return false;
    }
}
