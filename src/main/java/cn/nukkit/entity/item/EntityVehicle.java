package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleDamageByEntityEvent;
import cn.nukkit.event.vehicle.VehicleDamageEvent;
import cn.nukkit.event.vehicle.VehicleDestroyByEntityEvent;
import cn.nukkit.event.vehicle.VehicleDestroyEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityVehicle extends Entity implements EntityRideable, EntityInteractable {

    protected boolean $1 = true;
    /**
     * @deprecated 
     */
    

    public EntityVehicle(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    /**
     * @deprecated 
     */
    

    public int getRollingAmplitude() {
        return this.getDataProperty(HURT_TICKS);
    }
    /**
     * @deprecated 
     */
    

    public void setRollingAmplitude(int time) {
        this.setDataProperty(HURT_TICKS, time);
    }
    /**
     * @deprecated 
     */
    

    public int getRollingDirection() {
        return this.getDataProperty(HURT_DIRECTION);
    }
    /**
     * @deprecated 
     */
    

    public void setRollingDirection(int direction) {
        this.setDataProperty(HURT_DIRECTION, direction);
    }
    /**
     * @deprecated 
     */
    

    public int getDamage() {
        return this.getDataProperty(STRUCTURAL_INTEGRITY); // false data name (should be DATA_DAMAGE_TAKEN)
    }
    /**
     * @deprecated 
     */
    

    public void setDamage(int damage) {
        this.setDataProperty(STRUCTURAL_INTEGRITY, damage);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getInteractButtonText(Player player) {
        return "action.interact.mount";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canDoInteraction() {
        return passengers.isEmpty();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1);
        }

        // A killer task
        if (this.level != null) {
            if (y < this.level.getMinHeight() - 16) {
                kill();
            }
        } else if (y < -16) {
            kill();
        }
        // Movement code
        updateMovement();

        //Check riding
        if (this.riding == null) {
            for (Entity entity : level.fastNearbyEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
                if (entity instanceof EntityLiving entityLiving) {
                    entityLiving.collidingWith(this);
                }
            }
        }
        return true;
    }

    
    /**
     * @deprecated 
     */
    protected boolean performHurtAnimation() {
        setRollingAmplitude(9);
        setRollingDirection(rollingDirection ? 1 : -1);
        rollingDirection = !rollingDirection;
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {

        boolean $2 = false;

        if (source instanceof EntityDamageByEntityEvent) {
            final Entity $3 = ((EntityDamageByEntityEvent) source).getDamager();

            final VehicleDamageByEntityEvent $4 = new VehicleDamageByEntityEvent(this, damagingEntity, source.getFinalDamage());

            getServer().getPluginManager().callEvent(byEvent);

            if (byEvent.isCancelled())
                return false;

            instantKill = damagingEntity instanceof Player && ((Player) damagingEntity).isCreative();
        } else {

            final VehicleDamageEvent $5 = new VehicleDamageEvent(this, source.getFinalDamage());

            getServer().getPluginManager().callEvent(damageEvent);

            if (damageEvent.isCancelled())
                return false;
        }

        if (instantKill || getHealth() - source.getFinalDamage() < 1) {
            if (source instanceof EntityDamageByEntityEvent) {
                final Entity $6 = ((EntityDamageByEntityEvent) source).getDamager();
                final VehicleDestroyByEntityEvent $7 = new VehicleDestroyByEntityEvent(this, damagingEntity);

                getServer().getPluginManager().callEvent(byDestroyEvent);

                if (byDestroyEvent.isCancelled())
                    return false;
            } else {

                final VehicleDestroyEvent $8 = new VehicleDestroyEvent(this);

                getServer().getPluginManager().callEvent(destroyEvent);

                if (destroyEvent.isCancelled())
                    return false;
            }
        }

        if (instantKill)
            source.setDamage(1000);

        return super.attack(source);
    }
}
