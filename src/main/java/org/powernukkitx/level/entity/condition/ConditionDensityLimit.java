package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.NukkitMath;

import java.util.Map;

public class ConditionDensityLimit extends Condition {

    private final String entityId;
    public final int limit;
    public final int radius;

    public ConditionDensityLimit(String entityId, int limit) {
        this(entityId, limit, 32);
    }

    public ConditionDensityLimit(String entityId, int limit, int radius) {
        super("minecraft:density_limit");
        this.entityId = entityId;
        this.limit = limit;
        this.radius = radius;
    }

    @Override
    public boolean evaluate(Block block) {
        if (limit <= 0) {
            return false;
        }
        if (radius <= 0) {
            return true;
        }

        Level level = block.getLevel();
        double radiusSquared = (double) radius * radius;
        double invChunkSize = 1.0d / Level.CHUNK_SIZE;
        int minChunkX = NukkitMath.floorDouble((block.x - radius) * invChunkSize);
        int maxChunkX = NukkitMath.floorDouble((block.x + radius) * invChunkSize);
        int minChunkZ = NukkitMath.floorDouble((block.z - radius) * invChunkSize);
        int maxChunkZ = NukkitMath.floorDouble((block.z + radius) * invChunkSize);

        int count = 0;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                Map<Long, Entity> entities = level.getChunkEntities(chunkX, chunkZ, false);
                for (Entity entity : entities.values()) {
                    if (entityId.equals(entity.getIdentifier())
                            && entity.distanceSquared(block) < radiusSquared
                            && ++count >= limit) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
