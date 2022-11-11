package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.entity.Entity;
import com.dfsek.terra.api.util.vector.Vector3;
import com.dfsek.terra.api.world.ServerWorld;

@PowerNukkitXOnly
@Since("1.19.20-r4")
public class PNXEntity implements Entity {
    private cn.nukkit.entity.Entity nukkitEntity;
    private ServerWorld world;

    public PNXEntity(cn.nukkit.entity.Entity nukkitEntity, ServerWorld world) {
        this.nukkitEntity = nukkitEntity;
        this.world = world;
    }

    @Override
    public Vector3 position() {
        return Vector3.of(nukkitEntity.getX(), nukkitEntity.getY(), nukkitEntity.getZ());
    }

    @Override
    public void position(Vector3 vector3) {
        nukkitEntity.setX(vector3.getX());
        nukkitEntity.setY(vector3.getY());
        nukkitEntity.setZ(vector3.getZ());
    }

    @Override
    public void world(ServerWorld serverWorld) {
        nukkitEntity.setLevel(((PNXServerWorld)serverWorld).level());
        world = serverWorld;
    }

    @Override
    public ServerWorld world() {
        return world;
    }

    @Override
    public Object getHandle() {
        return nukkitEntity;
    }

    public cn.nukkit.entity.Entity entity() {
        return nukkitEntity;
    }
}
