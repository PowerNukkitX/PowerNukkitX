package org.powernukkitx.entity.ai;

import java.util.EnumSet;
import java.util.Set;

/**
 * Holds some global parameters of the AI framework.
 */


public final class EntityAI {
    private static long routeParticleSpawnInterval = 500;//ms

    private static final Set<DebugOption> debugOptions = EnumSet.noneOf(DebugOption.class);

    private EntityAI() {/*cannot be instantiated*/}

    public static void setDebugOption(DebugOption option, boolean open) {
        if (open) debugOptions.add(option);
        else debugOptions.remove(option);
    }

    public static boolean hasDebugOptions() {
        return !debugOptions.isEmpty();
    }

    public static boolean checkDebugOption(DebugOption option) {
        return debugOptions.contains(option);
    }

    /**
     * Sets route particle spawn interval.(Unit millisecond)
     *
     * @param routeParticleSpawnInterval the route particle spawn interval
     */
    public static void setRouteParticleSpawnInterval(long routeParticleSpawnInterval) {
        EntityAI.routeParticleSpawnInterval = routeParticleSpawnInterval;
    }

    /**
     * Gets route particle spawn interval.
     *
     * @return the route particle spawn interval
     */
    public static long getRouteParticleSpawnInterval() {
        return routeParticleSpawnInterval;
    }

    public enum DebugOption {
        /**
         * Show route waypoints.
         */
        ROUTE,
        /**
         * Show the behavior state in the mob's name tag.
         */
        BEHAVIOR,
        /**
         * Allow right-clicking a mob with a stick to query its memory state.
         */
        MEMORY
    }
}
