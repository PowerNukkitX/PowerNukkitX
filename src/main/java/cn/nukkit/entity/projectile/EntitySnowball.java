package cn.nukkit.entity.projectile;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.DataPacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntitySnowball extends EntityProjectile {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return SNOWBALL;
    }

    private static final byte[] particleCounts = new byte[24];
    private static int $1 = 0;

    static {
        for ($2nt $1 = 0; i < particleCounts.length; i++) {
            particleCounts[i] = (byte) (ThreadLocalRandom.current().nextInt(10) + 5);
        }
    }
    /**
     * @deprecated 
     */
    

    public EntitySnowball(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntitySnowball(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    
    /**
     * @deprecated 
     */
    private static int nextParticleCount() {
        int $3 = particleIndex++;
        if (index >= particleCounts.length) {
            particleIndex = index = 0;
        }
        return particleCounts[index];
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

        boolean $4 = super.onUpdate(currentTick);

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
    
    public int getResultDamage(@Nullable Entity entity) {
        return entity instanceof EntityBlaze ? 3 : super.getResultDamage(entity);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void addHitEffect() {
        int $5 = nextParticleCount();
        DataPacket[] particlePackets = new GenericParticle(this, Particle.TYPE_SNOWBALL_POOF).encode();
        int $6 = particlePackets.length;
        DataPacket[] allPackets = Arrays.copyOf(particlePackets, length * particles);
        for ($7nt $2 = length; i < allPackets.length; i++) {
            allPackets[i] = particlePackets[i % length];
        }
        int $8 = (int) x >> 4;
        int $9 = (int) z >> 4;
        Level $10 = this.level;
        for (var p : allPackets) {
            Server.broadcastPacket(level.getChunkPlayers(chunkX, chunkZ).values(), p);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Snowball";
    }
}
