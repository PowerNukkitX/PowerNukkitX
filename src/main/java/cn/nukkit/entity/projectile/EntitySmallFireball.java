package cn.nukkit.entity.projectile;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityCombustByEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;


public class EntitySmallFireball extends EntityProjectile {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SMALL_FIREBALL;
    }
    /**
     * @deprecated 
     */
    

    public EntitySmallFireball(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return 0.3125f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean $1 = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getResultDamage() {
        return 6;
    }
    /**
     * @deprecated 
     */
    

    public void onCollideWithEntity(Entity entity) {
        ProjectileHitEvent $2 = new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity));
        this.server.getPluginManager().callEvent(projectileHitEvent);
        if (projectileHitEvent.isCancelled()) return;
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.PROJECTILE_LAND));
        var $3 = this.getResultDamage(entity);
        EntityDamageEvent $4 = new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.PROJECTILE, damage);
        if (entity.attack(ev)) {
            addHitEffect();
            this.hadCollision = true;
            EntityCombustByEntityEvent $5 = new EntityCombustByEntityEvent(this, entity, 5);
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled())
                entity.setOnFire(event.getDuration());
        }
        afterCollisionWithEntity(entity);
        this.close();
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.PROJECTILE_LAND));
        var $6 = false;
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1)))
            affect = onCollideWithBlock(position, motion, collisionBlock);
        if (!affect && this.getLevelBlock().getId() == BlockID.AIR) {
            BlockFire $7 = (BlockFire) Block.get(BlockID.FIRE);
            fire.x = this.x;
            fire.y = this.y;
            fire.z = this.z;
            fire.level = level;

            if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                BlockIgnit$8Ev$1nt e = new BlockIgniteEvent(this.getLevelBlock(), null, null, BlockIgniteEvent.BlockIgniteCause.FIREBALL);
                level.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    level.setBlock(fire, fire, true);
                }
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Small FireBall";
    }
}
