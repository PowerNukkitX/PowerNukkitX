package cn.nukkit.entity.ai.executor.enderdragon;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.level.Location;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;


public class StrafeExecutor implements EntityControl, IBehaviorExecutor {

    private boolean fired = false;

    public StrafeExecutor() {
    }

    @Override
    public boolean execute(EntityIntelligent entity) {

        if (fired) return false;

        Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
        if (player == null) return false;
        setLookTarget(entity, player);
        setRouteTarget(entity, player);

        if (entity.distance(player) <= 64) {

            Vector3 toPlayerVector = new Vector3(player.x - entity.x, player.y - entity.y, player.z - entity.z).normalize();

            Location fireballLocation = entity.getLocation().add(toPlayerVector.multiply(5));
            double yaw = BVector3.getYawFromVector(toPlayerVector);
            double pitch = BVector3.getPitchFromVector(toPlayerVector);
            final NbtMap nbt = NbtMap.builder()
                    .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                    fireballLocation.x,
                                    fireballLocation.y,
                                    fireballLocation.z
                            )
                    ).putList("Motion", NbtType.DOUBLE, Arrays.asList(
                                    -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI),
                                    -Math.sin(pitch / 180 * Math.PI),
                                    Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI)
                            )
                    ).putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f)
                    ).build();

            Entity projectile = Entity.createEntity(EntityID.DRAGON_FIREBALL, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);
            projectile.spawnToAll();
            this.fired = true;
            return false;
        }
        return true;
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        Player player = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
        if (player == null) return;
        setLookTarget(entity, player);
        setRouteTarget(entity, player);
        this.fired = false;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        entity.getMemoryStorage().clear(CoreMemoryTypes.LAST_ENDER_CRYSTAL_DESTROY);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

}
