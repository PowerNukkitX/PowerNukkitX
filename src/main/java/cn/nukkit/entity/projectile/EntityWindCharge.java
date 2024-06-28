package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.item.ItemID.WIND_CHARGE;

public class EntityWindCharge extends EntityProjectile{

    public EntityWindCharge(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityWindCharge(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public @NotNull String getIdentifier() {
        return WIND_CHARGE_PROJECTILE;
    }

    @Override
    protected boolean onCollideWithBlock(Position position, Vector3 motion, Block collisionBlock) {

        if (this.shootingEntity instanceof Player player) {
            //system motion
        }
        level.addLevelSoundEvent(position.add(0, 1), LevelSoundEventPacket.SOUND_WIND_CHARGE_BURST);
        this.kill();

        return true;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        //system motion
        level.addLevelSoundEvent(entity.getPosition().add(0, 1), LevelSoundEventPacket.SOUND_WIND_CHARGE_BURST);
        this.level.addParticle(new GenericParticle(this, Particle.TYPE_WIND_EXPLOSION));
        this.kill();
    }

    @Override
    protected void addHitEffect() {
        this.level.addParticle(new GenericParticle(this, Particle.TYPE_WIND_EXPLOSION));
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
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    public float getLength() {
        return 0.3125f;
    }

    @Override
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    protected float getGravity() {
        return 0.00f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public String getOriginalName() {
        return "Wind Charge Projectile";
    }
}
