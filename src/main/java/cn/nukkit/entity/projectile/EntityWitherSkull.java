package cn.nukkit.entity.projectile;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityWither;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;


public class EntityWitherSkull extends EntityProjectile implements EntityExplosive {

    @Override
    @NotNull public String getIdentifier() {
        return WITHER_SKULL;
    }

    public EntityWitherSkull(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected double getDamage() {
        return switch (this.getServer().getDifficulty()) {
            case 1:
                yield 5;
            case 2:
                yield 8;
            case 3:
                yield 12;
            default:
                yield 0;
        };
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

    protected float getStrength() {
        return 1.2f;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if(!(entity instanceof EntityWither)) {
            explode();
            entity.addEffect(Effect.get(EffectType.WITHER).setDuration(200));
            super.onCollideWithEntity(entity);
        }
    }

    @Override
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.PROJECTILE_LAND));
        boolean affect = false;
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1)))
            affect = onCollideWithBlock(position, motion, collisionBlock);
        if (!affect && this.getLevelBlock().isAir()) {
            explode();
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public String getOriginalName() {
        return "Wither Skull";
    }

    @Override
    public void explode() {
        if (this.closed) {
            return;
        }
        this.close();
        EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, getStrength());
        ev.setFireChance(0);
        this.server.getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this.shootingEntity);
            explosion.setFireChance(ev.getFireChance());
            if (ev.isBlockBreaking() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explosion.explodeA();
            }
            explosion.explodeB();
        }
    }
}
