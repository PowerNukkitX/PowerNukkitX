package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 水中游泳动物
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntitySwimmingAnimal extends EntityAnimal {
    public EntitySwimmingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            source.setCancelled(true);
            return false;
        }
        return super.attack(source);
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
    public void updateMovement() {
        super.updateMovement();
        //在水中不受到重力以及浮力，不允许motionY留到下一次计算
        if (isInsideOfWater())
            this.motionY = 0;
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
