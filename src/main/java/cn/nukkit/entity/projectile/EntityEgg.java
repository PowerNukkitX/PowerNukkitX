package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemEgg;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityEgg extends EntityProjectile {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return EGG;
    }
    /**
     * @deprecated 
     */
    

    public EntityEgg(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityEgg(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.25f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return 0.25f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.25f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getGravity() {
        return 0.03f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getDrag() {
        return 0.01f;
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
    protected void addHitEffect() {
        int $2 = ThreadLocalRandom.current().nextInt(10) + 5;
        ItemEgg $3 = new ItemEgg();
        for ($4nt $1 = 0; i < particles; i++) {
            level.addParticle(new ItemBreakParticle(this, egg));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Egg";
    }
}
