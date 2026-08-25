package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.NukkitMath;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * The constructor accepts a Set of Integer to target function {@code Function<T, Boolean> target} to search for the nearest target entity, and the final result is saved to {@code List<MemoryType<Entity>> memories}.
 */


public class NearestTargetEntitySensor<T extends Entity> implements ISensor {

    protected double minRange;

    protected double maxRange;

    protected int period;

    protected Function<T, Boolean>[] allTargetFunction;

    protected List<MemoryType<Entity>> memories;

    /**
     * Without specifying the target function, all results will be stored in the first memory by default
     *
     * @see #NearestTargetEntitySensor(double, double, int, List, Function[])
     */
    public NearestTargetEntitySensor(double minRange, double maxRange, List<MemoryType<Entity>> memories) {
        this(minRange, maxRange, 1, memories, (Function<T, Boolean>) null);
    }

    /**
     * @param minRange          Minimum Search Range
     * @param maxRange          Maximum Search Range
     * @param period            Senor execute period
     * @param allTargetFunction Receives a Set that set the results filtered by the specified target function to the memory of the specified index, the target function accepts a parameter T and returns a Boolean
     * @param memories          Memory class type for saving results
     */
    @SafeVarargs
    public NearestTargetEntitySensor(double minRange, double maxRange, int period, List<MemoryType<Entity>> memories, Function<T, Boolean>... allTargetFunction) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.period = period;
        if (allTargetFunction == null) this.allTargetFunction = null;
        else {
            if (memories.size() >= 1 && allTargetFunction.length == memories.size()) {
                this.allTargetFunction = allTargetFunction;
            } else
                throw new IllegalArgumentException("All Target Function must correspond to memories one by one");
        }
        this.memories = memories;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        double minRangeSquared = this.minRange * this.minRange;
        double maxRangeSquared = this.maxRange * this.maxRange;
        Level level = entity.getLevel();
        int minChunkX = NukkitMath.floorDouble((entity.x - this.maxRange - 2) * 0.0625);
        int maxChunkX = NukkitMath.ceilDouble((entity.x + this.maxRange + 2) * 0.0625);
        int minChunkZ = NukkitMath.floorDouble((entity.z - this.maxRange - 2) * 0.0625);
        int maxChunkZ = NukkitMath.ceilDouble((entity.z + this.maxRange + 2) * 0.0625);

        if (allTargetFunction == null && memories.size() == 1) {
            var currentMemory = memories.get(0);
            var current = entity.getMemoryStorage().get(currentMemory);
            if (current != null && current.isAlive()) return;

            //Find the nearest entity within range
            Entity nearest = null;
            double nearestDistanceSquared = Double.MAX_VALUE;
            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                    for (Entity p : level.getChunkEntities(chunkX, chunkZ, false).values()) {
                        double distanceSquared = entity.distanceSquared(p);
                        if (distanceSquared <= maxRangeSquared && distanceSquared >= minRangeSquared
                                && distanceSquared < nearestDistanceSquared && !p.equals(entity)) {
                            nearest = p;
                            nearestDistanceSquared = distanceSquared;
                        }
                    }
                }
            }

            if (nearest == null) {
                entity.getMemoryStorage().clear(currentMemory);
            } else entity.getMemoryStorage().put(currentMemory, nearest);
            return;
        }
        if (allTargetFunction != null) {
            int len = memories.size();
            Entity[] nearest = new Entity[len];
            double[] nearestDistanceSquared = new double[len];
            Arrays.fill(nearestDistanceSquared, Double.MAX_VALUE);

            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                    for (Entity p : level.getChunkEntities(chunkX, chunkZ, false).values()) {
                        double distanceSquared = entity.distanceSquared(p);
                        if (distanceSquared <= maxRangeSquared && distanceSquared >= minRangeSquared && !p.equals(entity)) {
                            for (int i = 0; i < len; ++i) {
                                if (distanceSquared >= nearestDistanceSquared[i]) continue;
                                @SuppressWarnings("unchecked")
                                T castedP = (T) p;
                                if (allTargetFunction[i].apply(castedP)) {
                                    nearest[i] = p;
                                    nearestDistanceSquared[i] = distanceSquared;
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < len; ++i) {
                var currentMemory = memories.get(i);
                var current = entity.getMemoryStorage().get(currentMemory);
                if (current != null && current.isAlive()) continue;

                if (nearest[i] == null) {
                    entity.getMemoryStorage().clear(currentMemory);
                } else entity.getMemoryStorage().put(currentMemory, nearest[i]);
            }
        }
    }

    @Override
    public int getPeriod() {
        return period;
    }
}