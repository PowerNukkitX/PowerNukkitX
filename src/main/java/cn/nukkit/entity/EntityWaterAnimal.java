package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 水中游泳动物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityWaterAnimal extends EntityAnimal {
    public EntityWaterAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.DROWNING){
            source.setCancelled(true);
            return false;
        }
        return super.attack(source);
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

    }

    @Override
    public void updateMovement() {
        super.updateMovement();
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
    }

    @Override
    public float getMovementSpeedAtBlock(Block block) {
        return getMovementSpeed();
    }
}
