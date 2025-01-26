package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;


public class EntityWindCharge extends EntityProjectile {

    public Entity directionChanged;

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

        if(collisionBlock instanceof BlockDoor
                || collisionBlock instanceof BlockTrapdoor
                || collisionBlock instanceof BlockFenceGate
                || collisionBlock instanceof BlockButton
                || collisionBlock instanceof BlockLever
                || collisionBlock instanceof BlockCandle
                || collisionBlock instanceof BlockCandleCake) {
            collisionBlock.onActivate(Item.AIR, null, getDirection(), 0, 0, 0);
        }

        if(collisionBlock instanceof BlockChorusFlower
                    || collisionBlock instanceof BlockDecoratedPot) {
            this.getLevel().useBreakOn(collisionBlock, Item.AIR);
        }

        for(Entity entity : level.getEntities()) {
            if(entity instanceof EntityLiving entityLiving) {
                if(entityLiving.distance(this) < getBurstRadius()) {
                    this.knockBack(entityLiving);
                }
            }
        }
        level.addLevelSoundEvent(position.add(0, 1), LevelSoundEventPacket.SOUND_WIND_CHARGE_BURST);
        this.kill();

        return true;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if(directionChanged != null) {
            if(directionChanged == entity) return;
        }
        entity.attack(new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.PROJECTILE, 1f));
        level.addLevelSoundEvent(entity.getPosition().add(0, 1), LevelSoundEventPacket.SOUND_WIND_CHARGE_BURST);
        this.level.addParticle(new GenericParticle(this, Particle.TYPE_WIND_EXPLOSION));
        knockBack(entity);
        this.kill();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.directionChanged == null && source instanceof EntityDamageByEntityEvent event) {
            this.directionChanged = event.getDamager();
            this.setMotion(event.getDamager().getDirectionVector());
        }
        return true;
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

    public double getBurstRadius() {
        return 2f;
    }

    public double getKnockbackStrength() {
        return 0.2f;
    }

    protected void knockBack(Entity entity) {
        Vector3 knockback = new Vector3(entity.motionX, entity.motionY, entity.motionZ);
        knockback.x /= 2d;
        knockback.y /= 2d;
        knockback.z /= 2d;
        knockback.x -= (this.getX() - entity.getX()) * getKnockbackStrength();
        knockback.y += 0.3f;
        knockback.z -= (this.getZ() - entity.getZ()) * getKnockbackStrength();

        entity.setMotion(knockback);
    }
}
