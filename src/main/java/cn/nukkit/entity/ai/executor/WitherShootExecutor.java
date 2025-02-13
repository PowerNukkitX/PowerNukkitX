package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class WitherShootExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;
    protected MemoryType<? extends Entity> targetMemory;

    public WitherShootExecutor(MemoryType<? extends Entity> targetMemory) {
        this.targetMemory = targetMemory;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Entity target = entity.getMemoryStorage().get(targetMemory);
        if(target == null) return false;
        tick++;
        if (tick <= 40) {
            if (tick % 10 == 0) {
                spawn(entity, tick == 40);
            }
            setRouteTarget(entity, entity);
            setLookTarget(entity, target);
            return true;
        } else {
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_TIME, entity.getLevel().getTick());
            return false;
        }
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        removeRouteTarget(entity);
        tick = 0;
    }

    protected void spawn(Entity entity, boolean charged) {
        Location fireballLocation = entity.getLocation();
        fireballLocation.add(entity.getDirectionVector());
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(fireballLocation.x))
                        .add(new DoubleTag(fireballLocation.y))
                        .add(new DoubleTag(fireballLocation.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(-Math.sin(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(-Math.sin(entity.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(Math.cos(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI))))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw))
                        .add(new FloatTag((float) -entity.pitch)))
                .putDouble("damage", 2);

        Entity projectile = Entity.createEntity(charged ? EntityID.WITHER_SKULL_DANGEROUS : EntityID.WITHER_SKULL, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);
        if (projectile == null) {
            return;
        }
        if(projectile instanceof EntitySmallFireball fireball) {
            fireball.shootingEntity = entity;
        }

        ProjectileLaunchEvent projectev = new ProjectileLaunchEvent((EntityProjectile) projectile, entity);
        Server.getInstance().getPluginManager().callEvent(projectev);
        if (projectev.isCancelled()) {
            projectile.kill();
        } else {
            projectile.spawnToAll();
            entity.level.addSound(entity, Sound.MOB_WITHER_SHOOT);
        }
    }
}
