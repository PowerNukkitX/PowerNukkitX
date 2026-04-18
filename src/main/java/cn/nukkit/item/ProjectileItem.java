package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.Arrays;

/**
 * @author CreeperFace
 */
public abstract class ProjectileItem extends Item {

    public ProjectileItem(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    abstract public String getProjectileEntityType();

    abstract public float getThrowForce();

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                player.x,
                                player.y + player.getEyeHeight() - 0.30000000149011612,
                                player.z
                        )
                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(
                                directionVector.x,
                                directionVector.y,
                                directionVector.z
                        )
                ).putList("Rotation", NbtType.FLOAT, Arrays.asList((float) player.yaw, (float) player.pitch)
                ).build();

        this.correctNBT(nbt);

        Entity projectile = Entity.createEntity(this.getProjectileEntityType(), player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt, player);
        if (projectile != null) {
            projectile = correctProjectile(player, projectile);
            if (projectile == null) {
                return false;
            }

            projectile.setMotion(projectile.getMotion().multiply(this.getThrowForce()));

            if (projectile instanceof EntityProjectile) {
                ProjectileLaunchEvent ev = new ProjectileLaunchEvent((EntityProjectile) projectile, player);

                player.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    projectile.kill();
                } else {
                    if (!player.isCreative()) {
                        this.count--;
                    }
                    projectile.spawnToAll();
                    addThrowSound(player);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    protected void addThrowSound(Player player) {
        player.getLevel().addLevelSoundEvent(player, SoundEvent.THROW, -1, "minecraft:player", false, false);
    }

    protected Entity correctProjectile(Player player, Entity projectile) {
        return projectile;
    }

    protected void correctNBT(NbtMap nbt) {

    }
}
