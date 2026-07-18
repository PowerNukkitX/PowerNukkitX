package org.powernukkitx.entity.item;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.Particle;
import org.powernukkitx.level.particle.PotionSplashParticle;
import org.powernukkitx.nbt.tag.CompoundTag;
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


    public EntityXpBottle(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityXpBottle(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
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
    protected float getDefaultGravity() {
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
