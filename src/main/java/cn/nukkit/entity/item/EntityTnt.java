package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX
 */
public class EntityTnt extends Entity implements EntityExplosive {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return TNT;
    }

    protected int fuse;
    protected Entity source;
    /**
     * @deprecated 
     */
    

    public EntityTnt(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityTnt(IChunk chunk, CompoundTag nbt, Entity source) {
        super(chunk, nbt);
        this.source = source;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.98f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return 0.98f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.98f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCollide() {
        return false;
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Fuse")) {
            fuse = namedTag.getByte("Fuse");
        } else {
            fuse = 80;
        }

        this.setDataFlag(EntityFlag.IGNITED, true);
        this.setDataProperty(FUSE_TIME, fuse);

        this.getLevel().addSound(this, Sound.RANDOM_FUSE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Fuse", fuse);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        int $1 = currentTick - lastUpdate;

        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        if (fuse % 5 == 0) {
            this.setDataProperty(FUSE_TIME, fuse);
        }

        lastUpdate = currentTick;

        boolean $2 = entityBaseTick(tickDiff);

        if (isAlive()) {

            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float $3 = 1 - getDrag();

            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;

            updateMovement();

            if (onGround) {
                motionY *= -0.5;
                motionX *= 0.7;
                motionZ *= 0.7;
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)){
                    explode();
                }
                kill();
            }

        }

        return hasUpdate || fuse >= 0 || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void explode() {
        EntityExplosionPrimeEvent $4 = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion $5 = new Explosion(this, event.getForce(), this);
        explosion.setFireChance(event.getFireChance());
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.EXPLODE));
    }

    public Entity getSource() {
        return source;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Block of TNT";
    }
}
