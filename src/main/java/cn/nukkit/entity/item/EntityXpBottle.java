package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.PotionSplashParticle;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 */
public class EntityXpBottle extends EntityProjectile {
    @Override
    @NotNull
    public String getIdentifier() {
        return XP_BOTTLE;
    }


    public EntityXpBottle(IChunk chunk, NbtMap nbt) {
        this(chunk, nbt, null);
    }

    public EntityXpBottle(IChunk chunk, NbtMap nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }


    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.1f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.kill();
            this.close();
            hasUpdate = true;
        }

        if (this.isCollided) {
            this.explode();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.explode();
    }

    private void explode() {
        if (this.closed) return;
        this.kill();
        this.dropXp();
        this.close();
    }

    public void dropXp() {
        Particle particle2 = new PotionSplashParticle(this, 0x00385dc6);
        this.getLevel().addParticle(particle2);

        this.getLevel().addLevelSoundEvent(this, SoundEvent.GLASS);

        this.getLevel().dropExpOrb(this, ThreadLocalRandom.current().nextInt(3, 12));
    }

    @Override
    protected void addHitEffect() {
        this.getLevel().addSound(this, Sound.RANDOM_GLASS);
    }

    @Override
    public String getOriginalName() {
        return "Bottle o' Enchanting";
    }
}
