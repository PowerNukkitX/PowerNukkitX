package org.powernukkitx.entity.projectile;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFire;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.block.BlockIgniteEvent;
import org.powernukkitx.event.entity.EntityCombustByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.ProjectileHitEvent;
import org.powernukkitx.level.MovingObjectPosition;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;


public class EntitySmallFireball extends EntityProjectile {

    @Override
    @NotNull public String getIdentifier() {
        return SMALL_FIREBALL;
    }

    public EntitySmallFireball(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    public float getLength() {
        return 0.3125f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public int getResultDamage() {
        return 6;
    }

    public void onCollideWithEntity(Entity entity) {
        ProjectileHitEvent projectileHitEvent = new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity));
        this.server.getPluginManager().callEvent(projectileHitEvent);
        if (projectileHitEvent.isCancelled()) return;
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.PROJECTILE_LAND));
        var damage = this.getResultDamage(entity);
        EntityDamageEvent ev = new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.PROJECTILE, damage);
        if (entity.attack(ev)) {
            addHitEffect();
            this.hadCollision = true;
            EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled())
                entity.setOnFire(event.getDuration());
        }
        afterCollisionWithEntity(entity);
        this.close();
    }

    @Override
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.PROJECTILE_LAND));
        var affect = false;
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1)))
            affect = onCollideWithBlock(position, motion, collisionBlock);
        if (!affect && this.getLevelBlock().getId() == BlockID.AIR) {
            BlockFire fire = (BlockFire) Block.get(BlockID.FIRE);
            fire.x = this.x;
            fire.y = this.y;
            fire.z = this.z;
            fire.level = level;

            if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                BlockIgniteEvent e = new BlockIgniteEvent(this.getLevelBlock(), null, null, BlockIgniteEvent.BlockIgniteCause.FIREBALL);
                level.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    level.setBlock(fire, fire, true);
                }
            }
        }
    }

    @Override
    public String getOriginalName() {
        return "Small FireBall";
    }
}
