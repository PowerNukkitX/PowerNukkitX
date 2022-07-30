package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
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

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }
}
