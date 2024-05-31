package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import org.jetbrains.annotations.NotNull;

public class EntityEnderPearl extends EntityProjectile {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return ENDER_PEARL;
    }
    /**
     * @deprecated 
     */
    

    public EntityEnderPearl(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityEnderPearl(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
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
        boolean $1 = super.onUpdate(currentTick);

        if (this.isCollided && this.shootingEntity instanceof Player) {
            boolean $2 = false;
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
    /**
     * @deprecated 
     */
    
    public void onCollideWithEntity(Entity entity) {
        if (this.shootingEntity instanceof Player) {
            teleport();
        }
        super.onCollideWithEntity(entity);
    }

    
    /**
     * @deprecated 
     */
    private void teleport() {
        if (!this.level.equals(this.shootingEntity.getLevel())) {
            return;
        }

        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_TELEPORT_ENDERPEARL);
        this.shootingEntity.teleport(new Vector3(NukkitMath.floorDouble(this.x) + 0.5, this.y, NukkitMath.floorDouble(this.z) + 0.5), TeleportCause.ENDER_PEARL);
        if ((((Player) this.shootingEntity).getGamemode() & 0x01) == 0) {
            this.shootingEntity.attack(new EntityDamageByEntityEvent(this, shootingEntity, EntityDamageEvent.DamageCause.PROJECTILE, 5f, 0f));
        }
        this.level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_TELEPORT);
        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_TELEPORT_ENDERPEARL);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Ender Pearl";
    }
}
