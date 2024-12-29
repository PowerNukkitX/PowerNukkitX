package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@AllArgsConstructor
public class TeleportExecutor implements IBehaviorExecutor {

    int maxDistance;
    int minDistance;
    int maxTries = 16;

    private Location find(Location location) {
        int distance = maxDistance-minDistance;
        double dx = location.x + ThreadLocalRandom.current().nextInt(-distance, distance) + minDistance;
        double dz = location.z + ThreadLocalRandom.current().nextInt(-distance, distance) + minDistance;
        Vector3 pos = new Vector3(Math.floor(dx), (int) Math.floor(location.y + 0.1) + maxDistance, Math.floor(dz));
        for (int y = Math.min(location.getLevel().getMaxHeight(), (int) pos.y); y > location.getLevel().getMinHeight(); y--) {
            Block block = location.getValidLevel().getBlock((int) dx, y, (int) dz);
            if(block.isSolid()) {
                return block.up().getLocation();
            }
        }
        return location;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Location location = entity.getLocation();
        for(int i = 0; i < maxTries; i++) {
            if(location.distance(entity) < minDistance) {
                location = find(entity.getLocation());
            } else break;
        }
        if(entity.distance(location) > minDistance) {
            entity.teleport(location);
            location.level.addSound(location, Sound.MOB_ENDERMEN_PORTAL);
        }
        return true;
    }

}
