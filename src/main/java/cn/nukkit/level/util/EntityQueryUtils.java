package cn.nukkit.level.util;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityQueryOptions;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class EntityQueryUtils {

    private EntityQueryUtils() {}

    private static boolean hasAllTags(Entity e, Collection<String> tags) {
        for (String t : tags) {
            if (!e.hasTag(t)) return false;
        }
        return true;
    }

    private static boolean hasAnyExcludedTag(Entity e, Collection<String> exclude) {
        for (String t : exclude) {
            if (e.hasTag(t)) return true;
        }
        return false;
    }

    public static void filterNonSpatial(List<Entity> list, EntityQueryOptions o) {
        Predicate<Entity> p = e -> true;

        if (o.typeClass != null) {
            p = p.and(o.typeClass::isInstance);
        }

        if (o.nameTagEquals != null) {
            p = p.and(e -> o.nameTagEquals.equals(e.getNameTag()));
        }

        if (o.tags != null && !o.tags.isEmpty()) {
            p = p.and(e -> hasAllTags(e, o.tags));
        }

        if (o.excludeTags != null && !o.excludeTags.isEmpty()) {
            p = p.and(e -> !hasAnyExcludedTag(e, o.excludeTags));
        }

        if (o.predicate != null) {
            p = p.and(o.predicate);
        }

        list.removeIf(p.negate());
    }

    private static void sortByDistanceAscending(List<Entity> list, Vector3 center) {
        final double cx = center.x, cy = center.y, cz = center.z;
        list.sort((a, b) -> {
            double dax = a.x - cx, day = a.y - cy, daz = a.z - cz;
            double dbx = b.x - cx, dby = b.y - cy, dbz = b.z - cz;
            double da = dax * dax + day * day + daz * daz;
            double db = dbx * dbx + dby * dby + dbz * dbz;
            return Double.compare(da, db);
        });
    }

    private static void sortByDistanceDescending(List<Entity> list, Vector3 center) {
        final double cx = center.x, cy = center.y, cz = center.z;
        list.sort((a, b) -> {
            double dax = a.x - cx, day = a.y - cy, daz = a.z - cz;
            double dbx = b.x - cx, dby = b.y - cy, dbz = b.z - cz;
            double da = dax * dax + day * day + daz * daz;
            double db = dbx * dbx + dby * dby + dbz * dbz;
            return Double.compare(db, da);
        });
    }

    private static void trimTo(List<Entity> list, int keep) {
        if (keep >= 0 && list.size() > keep) {
            list.subList(keep, list.size()).clear();
        }
    }

    public static void applyOrderingAndLimits(List<Entity> list, Vector3 center, EntityQueryOptions o) {
        if (list == null || list.isEmpty() || o == null) return;

        boolean hasCenter = (center != null);

        if (hasCenter && o.closest != null && o.closest > 0) {
            sortByDistanceAscending(list, center);
            trimTo(list, o.closest);
        } else if (hasCenter && o.farthest != null && o.farthest > 0) {
            sortByDistanceDescending(list, center);
            trimTo(list, o.farthest);
        }

        if (o.limit != null && o.limit > 0) {
        trimTo(list, o.limit);
        }
    }

    public static boolean needsLocation(EntityQueryOptions o) {
        if (o == null) return false;
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

        list.removeIf(e -> !withinDistanceBand(e, cx, cy, cz, minD2, maxD2));
    }

    private static boolean isAtBlock(Entity e, int x, int y, int z) {
        if (e == null) return false;
        return e.getFloorX() == x && e.getFloorY() == y && e.getFloorZ() == z;
    }

    public static void collectExactBlockMatches(Map<Long, Entity> map, Vector3 loc, List<Entity> out) {
        if (map == null || map.isEmpty() || loc == null) return;

        int lx = (int) Math.floor(loc.x);
        int ly = (int) Math.floor(loc.y);
        int lz = (int) Math.floor(loc.z);

        for (Entity e : map.values()) {
            if (isAtBlock(e, lx, ly, lz)) {
                out.add(e);
            }
        }
    }

    private static boolean withinDistanceBand(Entity e, double cx, double cy, double cz, double minD2, double maxD2) {
        double dx = e.x - cx;
        double dy = e.y - cy;
        double dz = e.z - cz;
        double d2 = dx * dx + dy * dy + dz * dz;

        if (maxD2 >= 0.0 && d2 > maxD2) return false;
        if (minD2 >= 0.0 && d2 < minD2) return false;
        return true;
    }

    private static boolean insideCuboid(Entity e, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        return e.x >= minX && e.x <= maxX
            && e.y >= minY && e.y <= maxY
            && e.z >= minZ && e.z <= maxZ;
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
        Predicate<Entity> p = e -> e != null;

        if (hasLocation) {
            p = p.and(e -> withinDistanceBand(e, cx0, cy0, cz0, minD2, maxD2));
            if (useCuboid) {
                p = p.and(e -> insideCuboid(e, vMinX, vMaxX, vMinY, vMaxY, vMinZ, vMaxZ));
            }
        }

        for (int cX = minCX; cX <= maxCX; ++cX) {
            for (int cZ = minCZ; cZ <= maxCZ; ++cZ) {
                Map<Long, Entity> map = level.getChunkEntities(cX, cZ, loadChunks);
                if (map == null || map.isEmpty()) continue;

                for (Entity e : map.values()) {
                    if (p.test(e)) {
                        out.add(e);
                    }
                }
            }
        }
    }
}