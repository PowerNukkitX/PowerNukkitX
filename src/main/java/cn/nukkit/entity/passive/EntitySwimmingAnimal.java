package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
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
    protected void handleGravity() {
        if (!this.onGround && !this.isInsideOfWater()) {
            this.motionY -= this.getGravity();
        } else if (this.isInsideOfWater())
            resetFallDistance();
    }

    @Override
    public float getGravity() {
        return super.getGravity();
    }

    @Override
    protected void handleFrictionMovement() {
        if (!isInsideOfWater()) super.handleFrictionMovement();
    }

    @Override
    protected void handleFloatingMovement() {

    }

    @Override
    public void updateMovement() {// 检测自由落体时间
        if (!this.onGround && !this.isInsideOfWater() && this.y < this.highestPosition) {
            this.fallingTick++;
        }
        super.updateMovement();
        //处理下落
        if (this.isInsideOfWater()) {
            this.motionX = 0;
            if (this.motionY < 0) {
                this.motionY += getGravity();
                if (this.motionY > 0) this.motionY = 0;
            }
            this.motionZ = 0;
        }
    }

    @Override
    public float getMovementSpeedAtBlock(Block block) {
        return block instanceof BlockLiquid liquid ? getMovementSpeed() : super.getMovementSpeedAtBlock(block) * 0.2f;
    }

    //巨型体系
    public boolean isLarge() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_LARGE);
    }
}
