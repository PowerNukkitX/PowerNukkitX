package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityEndermite;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class EntityEnderPearl extends EntityProjectile {

    @Override
    @NotNull
    public String getIdentifier() {
        return ENDER_PEARL;
    }

    public EntityEnderPearl(IChunk chunk, NbtMap nbt) {
        this(chunk, nbt, null);
    }

    public EntityEnderPearl(IChunk chunk, NbtMap nbt, Entity shootingEntity) {
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

        if (this.isCollided && this.shootingEntity instanceof Player) {
            boolean portal = false;
            for (Block collided : this.getCollisionBlocks()) {
                if (collided.getId() == Block.PORTAL) {
                    portal = true;
                }
            }
            if (!portal) {
                teleport();
            }
        }

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (this.shootingEntity instanceof Player) {
            teleport();
        }
        super.onCollideWithEntity(entity);
    }

    private void teleport() {
        if (!this.level.equals(this.shootingEntity.getLevel())) {
            return;
        }

        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEvent.SOUND_TELEPORT_ENDERPEARL);
        Vector3 destination = new Vector3(NukkitMath.floorDouble(this.x) + 0.5, this.y + 1, NukkitMath.floorDouble(this.z) + 0.5);
        this.shootingEntity.teleport(destination, TeleportCause.ENDER_PEARL);
        if ((((Player) this.shootingEntity).getGamemode() & 0x01) == 0) {
            this.shootingEntity.attack(new EntityDamageByEntityEvent(this, shootingEntity, EntityDamageEvent.DamageCause.PROJECTILE, 5f, 0f));
        }
        this.level.addLevelEvent(this, LevelEvent.PARTICLE_TELEPORT);
        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEvent.SOUND_TELEPORT_ENDERPEARL);
        if (this.level.getGameRules().getBoolean(GameRule.DO_MOB_SPAWNING)) {
            if (ThreadLocalRandom.current().nextInt(1, 20) == 1) {
                EntityEndermite endermite = (EntityEndermite) Entity.createEntity(Entity.ENDERMITE,
                        level.getChunk(destination.getFloorX() >> 4, destination.getFloorZ() >> 4), NbtMap.builder()
                                .putList("Pos", NbtType.DOUBLE, Arrays.asList(destination.getX() + 0.5, destination.getY() + 0.0625d, destination.getZ() + 0.5))
                                .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                                .putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f))
                                .build()
                );
                endermite.spawnToAll();
            }
        }
    }

    @Override
    public String getOriginalName() {
        return "Ender Pearl";
    }
}
