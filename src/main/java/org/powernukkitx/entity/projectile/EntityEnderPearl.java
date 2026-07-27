package org.powernukkitx.entity.projectile;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.mob.EntityEndermite;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.player.PlayerTeleportEvent.TeleportCause;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class EntityEnderPearl extends EntityProjectile {

    @Override
    @NotNull
    public String getIdentifier() {
        return ENDER_PEARL;
    }

    public EntityEnderPearl(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityEnderPearl(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
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
        Position oldPosition = getPosition();
        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.isCollided && this.shootingEntity instanceof Player) {
            boolean portal = false;
            for (Block collided : this.getCollisionBlocks()) {
                if (collided.getId().equals(Block.PORTAL)) {
                    portal = true;
                }
            }
            if (!portal) {
                teleportOwner(oldPosition);
            }
        }

        if (this.age > 1200 || this.isCollided) {
            this.close();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (this.shootingEntity instanceof Player) {
            teleportOwner(getPosition());
        }
        super.onCollideWithEntity(entity);
    }

    private void teleportOwner(Vector3 destination) {
        if (!this.level.equals(this.shootingEntity.getLevel())) {
            return;
        }

        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEvent.SOUND_TELEPORT_ENDERPEARL);
        if(this.shootingEntity.teleport(destination, TeleportCause.ENDER_PEARL)) {
            if ((((Player) this.shootingEntity).getGamemode() & 0x01) == 0) {
                this.shootingEntity.attack(new EntityDamageByEntityEvent(this, shootingEntity, EntityDamageEvent.DamageCause.PROJECTILE, 5f, 0f));
            }
            this.level.addLevelEvent(this, LevelEvent.PARTICLE_TELEPORT);
            this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEvent.SOUND_TELEPORT_ENDERPEARL);
            if (this.level.getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
                if (ThreadLocalRandom.current().nextInt(1, 20) == 1) {
                    EntityEndermite endermite = (EntityEndermite) Entity.createEntity(Entity.ENDERMITE,
                        this.getChunk(), Entity.getDefaultNBT(destination)
                    );
                    endermite.spawnToAll();
                }
            }
        }
    }

    @Override
    public String getOriginalName() {
        return "Ender Pearl";
    }
}
