package cn.nukkit.entity.projectile;

import cn.nukkit.block.BlockFrame;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.types.LevelSoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class EntityEyeOfEnderSignal extends Entity {
    private static final String TAG_LIFE = "Life";
    private static final String TAG_SURVIVE_AFTER_DEATH = "SurviveAfterDeath";
    private static final String TAG_TARGET = "Target";
    private static final String TAG_TARGET_X = "x";
    private static final String TAG_TARGET_Y = "y";
    private static final String TAG_TARGET_Z = "z";

    private static final double TOO_FAR_SIGNAL_HEIGHT = 8.0;
    private static final double TOO_FAR_DISTANCE = 12.0;
    private static final int MAX_LIFE_TICKS = 80;
    private static final int SURVIVE_CHANCE_DIVISOR = 5;
    private Vector3 target;
    private int life;
    private boolean surviveAfterDeath;

    public EntityEyeOfEnderSignal(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return EYE_OF_ENDER_SIGNAL;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setDataFlag(EntityFlag.HAS_GRAVITY, false);
        this.setDataFlag(EntityFlag.HAS_COLLISION, false);

        this.life = this.namedTag.getInt(TAG_LIFE, 0);
        this.surviveAfterDeath = this.namedTag.getBoolean(TAG_SURVIVE_AFTER_DEATH);

        if (this.namedTag.contains(TAG_TARGET)) {
            CompoundTag targetTag = this.namedTag.getCompound(TAG_TARGET);
            this.target = new Vector3(
                    targetTag.getDouble(TAG_TARGET_X),
                    targetTag.getDouble(TAG_TARGET_Y),
                    targetTag.getDouble(TAG_TARGET_Z)
            );
        }
    }

    public void signalTo(Vector3 target) {
        Vector3 delta = target.subtract(this);
        double horizontalDistance = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (horizontalDistance > TOO_FAR_DISTANCE) {
            this.target = this.add(
                    delta.x / horizontalDistance * TOO_FAR_DISTANCE,
                    TOO_FAR_SIGNAL_HEIGHT,
                    delta.z / horizontalDistance * TOO_FAR_DISTANCE
            );
        } else {
            this.target = target.clone();
        }

        this.life = 0;
        this.surviveAfterDeath = ThreadLocalRandom.current().nextInt(SURVIVE_CHANCE_DIVISOR) > 0;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);
        if (!this.isAlive()) {
            return hasUpdate;
        }

        Vector3 oldMovement = this.getMotion();
        Vector3 nextPosition = this.getVector3().add(oldMovement);

        if (this.target != null) {
            this.setMotion(updateMovement(oldMovement, nextPosition, this.target));
        }

        Vector3 particleOrigin = nextPosition.subtract(this.getMotion().multiply(0.25));
        this.level.addParticleEffect(particleOrigin, this.isInsideOfWater() ? ParticleEffect.EYE_OF_ENDER_BUBBLE : ParticleEffect.BASIC_PORTAL);

        this.setPosition(nextPosition);
        this.updateMovement();
        hasUpdate = true;

        this.life++;
        if (this.life > MAX_LIFE_TICKS) {
            this.onLifeExpired();
            return true;
        }

        return hasUpdate;
    }

    private void onLifeExpired() {
        if (this.surviveAfterDeath) {
            this.level.dropItem(this, Item.get(Item.ENDER_EYE));
        } else {
            this.level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_EYE_OF_ENDER_DEATH);
            //      https://bugs.mojang.com/browse/MCPE/issues/MCPE-115646 (Game misses right sound file and plays this instead)
            this.level.addLevelSoundEvent(this, LevelSoundEvent.BREAK, BlockFrame.PROPERTIES.getDefaultState().blockStateHash());
        }
        this.kill();
    }

    private static Vector3 updateMovement(Vector3 oldMovement, Vector3 position, Vector3 target) {
        Vector3 horizontal = new Vector3(target.x - position.x, 0.0, target.z - position.z);
        double horizontalLength = Math.sqrt(horizontal.x * horizontal.x + horizontal.z * horizontal.z);
        if (horizontalLength < 1.0E-6) {
            return oldMovement;
        }

        double oldHorizontalSpeed = Math.sqrt(oldMovement.x * oldMovement.x + oldMovement.z * oldMovement.z);
        double wantedSpeed = oldHorizontalSpeed + (horizontalLength - oldHorizontalSpeed) * 0.0025;
        double movementY = oldMovement.y;

        if (horizontalLength < 1.0) {
            wantedSpeed *= 0.8;
            movementY *= 0.8;
        }

        double wantedMovementY = position.y - oldMovement.y < target.y ? 1.0 : -1.0;
        return horizontal.multiply(wantedSpeed / horizontalLength)
                .add(0.0, movementY + (wantedMovementY - movementY) * 0.015, 0.0);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt(TAG_LIFE, this.life);
        this.namedTag.putBoolean(TAG_SURVIVE_AFTER_DEATH, this.surviveAfterDeath);
        if (this.target != null) {
            this.namedTag.putCompound(TAG_TARGET, new CompoundTag()
                    .putDouble(TAG_TARGET_X, this.target.x)
                    .putDouble(TAG_TARGET_Y, this.target.y)
                    .putDouble(TAG_TARGET_Z, this.target.z));
        }
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public String getOriginalName() {
        return "Eye of Ender";
    }
}
