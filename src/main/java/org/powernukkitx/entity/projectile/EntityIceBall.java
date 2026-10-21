package org.powernukkitx.entity.projectile;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.GenericParticle;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The thrown ice ball. It flies like a snowball and shatters with its own particle and sound.
 *
 * @author xRookieFight
 * @since 15/10/2026
 */
public class EntityIceBall extends EntityProjectile {
    @Override
    @NotNull
    public String getIdentifier() {
        return ICE_BALL;
    }

    public EntityIceBall(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityIceBall(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
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
        return 0.03f;
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

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    protected void addHitEffect() {
        this.level.addParticle(new GenericParticle(this, ParticleType.ICE_BALL_BREAK));
        this.level.addLevelSoundEvent(this, SoundEvent.ICE_BALL_BREAK);
    }

    @Override
    public String getOriginalName() {
        return "Ice Ball";
    }
}
