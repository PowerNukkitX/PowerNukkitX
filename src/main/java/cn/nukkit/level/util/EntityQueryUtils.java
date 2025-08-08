package cn.nukkit.level.util;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityQueryOptions;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class EntityQueryUtils {

    private EntityQueryUtils() {}

    public static void filterNonSpatial(List<Entity> list, Vector3 center, EntityQueryOptions o) {
        Iterator<Entity> it = list.iterator();
        while (it.hasNext()) {
            Entity e = it.next();

            if (o.typeClass != null && !o.typeClass.isInstance(e)) {
                it.remove(); continue;
            }

            if (o.nameTagEquals != null) {
                String nt = e.getNameTag();
                if (nt == null || !nt.equals(o.nameTagEquals)) {
                    it.remove(); continue;
                }
            }

            if (o.tags != null && !o.tags.isEmpty()) {
                boolean ok = true;
                for (String t : o.tags) {
                    if (!e.hasTag(t)) { ok = false; break; }
                }
                if (!ok) { it.remove(); continue; }
            }

            if (o.excludeTags != null && !o.excludeTags.isEmpty()) {
                boolean bad = false;
                for (String t : o.excludeTags) {
                    if (e.hasTag(t)) { bad = true; break; }
                }
                if (bad) { it.remove(); continue; }
            }

            if (o.predicate != null && !o.predicate.test(e)) {
                it.remove();
            }
        }
    }

    public static void applyOrderingAndLimits(List<Entity> list, Vector3 center, EntityQueryOptions o) {
        if (center != null && o != null) {
            if (o.closest != null && o.closest > 0) {
                list.sort((a, b) -> Double.compare(dist2(a, center), dist2(b, center)));
                if (list.size() > o.closest) list.subList(o.closest, list.size()).clear();
            } else if (o.farthest != null && o.farthest > 0) {
                list.sort((a, b) -> Double.compare(dist2(b, center), dist2(a, center)));
                if (list.size() > o.farthest) list.subList(o.farthest, list.size()).clear();
            }
        }

        if (o != null && o.limit != null && o.limit > 0 && list.size() > o.limit) {
            list.subList(o.limit, list.size()).clear();
        }
    }

    public static double dist2(Entity e, Vector3 c) {
        double dx = e.x - c.x;
        double dy = e.y - c.y;
        double dz = e.z - c.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public static boolean needsLocation(EntityQueryOptions o) {
        return (o.volume != null)
            || (o.maxDistance != null)
            || (o.minDistance != null)
            || (o.closest != null && o.closest > 0)
            || (o.farthest != null && o.farthest > 0);
    }

    /**
     * If a location is provided but no other spatial constraints, default to exactLocationMatch.
     */
    public static boolean shouldDefaultToExact(EntityQueryOptions o) {
        if (o == null || o.location == null) return false;
        boolean hasOthers =
            (o.volume != null) ||
            (o.maxDistance != null) ||
            (o.minDistance != null) ||
            (o.closest != null && o.closest > 0) ||
            (o.farthest != null && o.farthest > 0) ||
            o.exactLocationMatch;
        return !hasOthers;
    }

    public static void filterByDistanceBand(List<Entity> list, Vector3 center, Double min, Double max) {
        if (center == null || (min == null && max == null) || list.isEmpty()) return;
        final double cx = center.x, cy = center.y, cz = center.z;
        final double minD2 = (min != null) ? (min * min) : -1.0;
        final double maxD2 = (max != null) ? (max * max) : -1.0;

        Iterator<Entity> it = list.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            double dx = e.x - cx;
            double dy = e.y - cy;
            double dz = e.z - cz;
            double d2 = dx * dx + dy * dy + dz * dz;
            if (maxD2 >= 0.0 && d2 > maxD2) { it.remove(); continue; }
            if (minD2 >= 0.0 && d2 < minD2) { it.remove(); }
        }
    }

    public static void collectExactBlockMatches(Map<Long, Entity> map, Vector3 loc, List<Entity> out) {
        if (map == null || map.isEmpty() || loc == null) return;
        int lx = (int) Math.floor(loc.x);
        int ly = (int) Math.floor(loc.y);
        int lz = (int) Math.floor(loc.z);

        for (Entity e : map.values()) {
            if (e == null) continue;
            if ((int) Math.floor(e.x) == lx
                    && (int) Math.floor(e.y) == ly
                    && (int) Math.floor(e.z) == lz) {
                out.add(e);
            }
        }
    }

    public static void collectEntitiesInChunks(
            Level level,
            int minCX, int maxCX, int minCZ, int maxCZ,
            boolean loadChunks,
            boolean hasLocation,
            double cx0, double cy0, double cz0,
            double minD2, double maxD2,
            boolean useCuboid,
            double vMinX, double vMaxX, double vMinY, double vMaxY, double vMinZ, double vMaxZ,
            List<Entity> out
    ) {
        for (int cX = minCX; cX <= maxCX; ++cX) {
            for (int cZ = minCZ; cZ <= maxCZ; ++cZ) {
                Map<Long, Entity> map = level.getChunkEntities(cX, cZ, loadChunks);
                if (map == null || map.isEmpty()) continue;

                for (Entity e : map.values()) {
                    if (e == null) continue;

                    if (hasLocation) {
                        double dx = e.x - cx0;
                        double dy = e.y - cy0;
                        double dz = e.z - cz0;
                        double d2 = dx * dx + dy * dy + dz * dz;

                        if (maxD2 >= 0.0 && d2 > maxD2) continue;
                        if (minD2 >= 0.0 && d2 < minD2) continue;

                        if (useCuboid) {
                            if (e.x < vMinX || e.x > vMaxX) continue;
                            if (e.y < vMinY || e.y > vMaxY) continue;
                            if (e.z < vMinZ || e.z > vMaxZ) continue;
                        }
                    }

                    out.add(e);
                }
            }
        }
    }
}
