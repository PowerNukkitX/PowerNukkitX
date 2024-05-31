package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityEnderCrystal;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.event.entity.EntityCombustByEntityEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityProjectile extends Entity {
    public static final int $1 = 0;
    public static final int $2 = 1;
    public static final int $3 = 2;

    public Entity shootingEntity;
    public boolean hadCollision;
    public boolean closeOnCollide;
    /**
     * It's inverted from {@link #getHasAge()} because of the poor architecture chosen by the original devs
     * on the entity construction and initialization. It's impossible to set it to true before
     * the initialization of the child classes.
     */
    private boolean noAge;
    /**
     * @deprecated 
     */
    

    public EntityProjectile(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityProjectile(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt);
        this.shootingEntity = shootingEntity;
        if (shootingEntity != null) {
            this.setDataProperty(OWNER_EID, shootingEntity.getId());
        }
    }

    
    /**
     * @deprecated 
     */
    protected double getDamage() {
        return namedTag.contains("damage") ? namedTag.getDouble("damage") : getBaseDamage();
    }

    
    /**
     * @deprecated 
     */
    protected double getBaseDamage() {
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public int getResultDamage(@Nullable Entity entity) {
        return getResultDamage();
    }
    /**
     * @deprecated 
     */
    

    public int getResultDamage() {
        return NukkitMath.ceilDouble(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }
    /**
     * @deprecated 
     */
    

    public void onCollideWithEntity(Entity entity) {
        ProjectileHitEvent $4 = new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity));
        this.server.getPluginManager().callEvent(projectileHitEvent);

        if (projectileHitEvent.isCancelled()) {
            return;
        }

        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.PROJECTILE_LAND));

        float $5 = this.getResultDamage(entity);

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        if (entity.attack(ev)) {
            addHitEffect();
            this.hadCollision = true;

            if (this.fireTicks > 0) {
                EntityCombustByEntityEvent $6 = new EntityCombustByEntityEvent(this, entity, 5);
                this.server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration());
                }
            }
        }
        afterCollisionWithEntity(entity);
        if (closeOnCollide) {
            this.close();
        }
    }

    
    /**
     * @deprecated 
     */
    protected void afterCollisionWithEntity(Entity entity) {

    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.closeOnCollide = true;
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
        if (this.namedTag.contains("Age") && !this.noAge) {
            this.age = this.namedTag.getShort("Age");
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCollideWith(Entity entity) {
        return (entity instanceof EntityLiving || entity instanceof EntityEnderCrystal || entity instanceof EntityMinecartAbstract || entity instanceof EntityBoat) && !this.onGround;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        if (!this.noAge) {
            this.namedTag.putShort("Age", this.age);
        }
    }

    
    /**
     * @deprecated 
     */
    protected void updateMotion() {
        this.motionY -= this.getGravity();
        this.motionX *= 1 - this.getDrag();
        this.motionZ *= 1 - this.getDrag();
    }

    /**
     * Filters the entities that collide with projectile
     *
     * @param entity the collide entity
     * @return the boolean
     */
    
    /**
     * @deprecated 
     */
    protected boolean collideEntityFilter(Entity entity) {
        if ((entity == this.shootingEntity && this.ticksLived < 5) ||
                (entity instanceof Player player && player.getGamemode() == Player.SPECTATOR)
                || (this.shootingEntity instanceof Player && Optional.ofNullable(this.shootingEntity.riding).map(e -> e.equals(entity)).orElse(false))) {
            return false;
        } else return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int $7 = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean $8 = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            MovingObjectPosition $9 = null;

            if (!this.isCollided) {
                updateMotion();
            }

            Vector3 $10 = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);

            Entity[] list = this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1), this);

            double $11 = Integer.MAX_VALUE;
            Entity $12 = null;

            for (Entity entity : list) {
                if (!collideEntityFilter(entity)) {
                    continue;
                }

                AxisAlignedBB $13 = entity.boundingBox.grow(0.3, 0.3, 0.3);
                MovingObjectPosition $14 = axisalignedbb.calculateIntercept(this, moveVector);

                if (ob == null) {
                    continue;
                }

                double $15 = this.distanceSquared(ob.hitVector);

                if (distance < nearDistance) {
                    nearDistance = distance;
                    nearEntity = entity;
                }
            }

            if (nearEntity != null) {
                movingObjectPosition = MovingObjectPosition.fromEntity(nearEntity);
            }

            if (movingObjectPosition != null) {
                if (movingObjectPosition.entityHit != null) {
                    onCollideWithEntity(movingObjectPosition.entityHit);
                    hasUpdate = true;
                    if (closed) {
                        return true;
                    }
                }
            }

            Position $16 = getPosition();
            Vector3 $17 = getMotion();
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), BlockFace.UP, this)));
                onCollideWithBlock(position, motion);
                addHitEffect();
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                updateRotation();
                hasUpdate = true;
            }

            this.updateMovement();
        }

        return hasUpdate;
    }
    /**
     * @deprecated 
     */
    

    public void updateRotation() {
        double $18 = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
        this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
        this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI;
    }
    /**
     * @deprecated 
     */
    

    public void inaccurate(float modifier) {
        Random $19 = ThreadLocalRandom.current();

        this.motionX += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionY += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionZ += rand.nextGaussian() * 0.007499999832361937 * modifier;
    }

    
    /**
     * @deprecated 
     */
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.PROJECTILE_LAND));
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            onCollideWithBlock(position, motion, collisionBlock);
        }
    }

    
    /**
     * @deprecated 
     */
    protected boolean onCollideWithBlock(Position position, Vector3 motion, Block collisionBlock) {
        return collisionBlock.onProjectileHit(this, position, motion);
    }

    
    /**
     * @deprecated 
     */
    protected void addHitEffect() {

    }
    /**
     * @deprecated 
     */
    

    public boolean getHasAge() {
        return !this.noAge;
    }
    /**
     * @deprecated 
     */
    

    public void setHasAge(boolean hasAge) {
        this.noAge = !hasAge;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void spawnToAll() {
        super.spawnToAll();
        //vibration: minecraft:projectile_shoot
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this.shootingEntity, this.clone(), VibrationType.PROJECTILE_SHOOT));
    }
}
