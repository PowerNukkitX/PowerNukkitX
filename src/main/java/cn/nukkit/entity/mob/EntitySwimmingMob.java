package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 水中游泳怪物
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntitySwimmingMob extends EntityMob {
    public EntitySwimmingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        super.attack(source);
        if (source.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            source.setCancelled(true);
            return false;
        }
        return true;
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
    protected void handleFloatingMovement() {
        //水生生物不受到浮力
    }

    @Override
    protected void handleGravity() {
        //在水中没有重力
        if (!this.onGround && !this.isInsideOfWater()) {
            this.motionY -= this.getGravity();
        } else if (this.isInsideOfWater())
            resetFallDistance();
    }

    @Override
    public boolean isFalling() {
        return !this.onGround && !this.isInsideOfWater() && this.y < this.highestPosition;
    }

    @Override
    public float getMovementSpeedAtBlock(Block block) {
        if (block instanceof BlockLiquid) {
            return getMovementSpeed();
        } else {
            //水生生物在陆地的速度只有原来的1/5
            return getMovementSpeed() * 0.2f;
        }
    }
}
