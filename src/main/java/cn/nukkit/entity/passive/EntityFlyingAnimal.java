package cn.nukkit.entity.passive;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 空中飞行动物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityFlyingAnimal extends EntityAnimal {
    public EntityFlyingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void handleGravity() {
        resetFallDistance();
    }

    @Override
    public float getGravity() {
        return 0;
    }

    @Override
    protected void handleFrictionMovement() {

    }

    @Override
    protected void handleFloatingMovement() {
        if (this.isTouchingWater()) {
            this.motionY += getMovementSpeed() * 0.3;
        }
    }

    @Override
    public void updateMovement() {
        super.updateMovement();
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
    }
}
