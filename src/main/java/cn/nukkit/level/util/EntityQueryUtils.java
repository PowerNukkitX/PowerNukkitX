package cn.nukkit.level.util;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityQueryOptions;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class EntityQueryUtils {

    private EntityQueryUtils() {}

    public static final class ChunkQuery {
        public int minCX, maxCX, minCZ, maxCZ;
        public boolean loadChunks;

        public ChunkQuery(int minCX, int maxCX, int minCZ, int maxCZ, boolean loadChunks) {
            this.minCX = minCX; this.maxCX = maxCX;
            this.minCZ = minCZ; this.maxCZ = maxCZ;
            this.loadChunks = loadChunks;
        }
    }

    public static final class SpatialFilter {
        public boolean hasLocation;
        public double cx, cy, cz;
        public double minD2 = -1.0, maxD2 = -1.0;
        public boolean useCuboid;
        public double vMinX, vMaxX, vMinY, vMaxY, vMinZ, vMaxZ;

        public SpatialFilter withCenter(Vector3 c) {
            if (c != null) {
                this.hasLocation = true;
                this.cx = c.x; this.cy = c.y; this.cz = c.z;
            }
            return this;
        }

        public SpatialFilter withDistanceBand(Double min, Double max) {
            this.minD2 = (min != null) ? min * min : -1.0;
            this.maxD2 = (max != null) ? max * max : -1.0;
            return this;
        }

        public SpatialFilter withCuboid(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
            this.useCuboid = true;
            this.vMinX = minX; this.vMaxX = maxX;
            this.vMinY = minY; this.vMaxY = maxY;
            this.vMinZ = minZ; this.vMaxZ = maxZ;
            return this;
        }
    }

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
        if (o == null || !hasAnyNonSpatialFilters(o)) return;

        Predicate<Entity> p = e -> true;

        boolean useFamilyInclude = (o.families != null && !o.families.isEmpty());
        if (useFamilyInclude) {
            p = p.and(e -> e.isAllFamilies(o.families));
        } else {
            p = andIf(p, o.typeClass != null, o.typeClass::isInstance);
        }

        if (o.excludeFamilies != null && !o.excludeFamilies.isEmpty()) {
            p = p.and(e -> !e.isAnyFamily(o.excludeFamilies));
        }

        p = andIf(p, o.nameTagEquals != null, e -> o.nameTagEquals.equals(e.getNameTag()));
        p = andIf(p, o.tags != null && !o.tags.isEmpty(), e -> hasAllTags(e, o.tags));
        p = andIf(p, o.excludeTags != null && !o.excludeTags.isEmpty(), e -> !hasAnyExcludedTag(e, o.excludeTags));
        p = andIf(p, o.predicate != null, o.predicate);

        list.removeIf(p.negate());
    }

    private static boolean hasAnyNonSpatialFilters(EntityQueryOptions o) {
        return o.typeClass != null
            || o.nameTagEquals != null
            || (o.tags != null && !o.tags.isEmpty())
            || (o.excludeTags != null && !o.excludeTags.isEmpty())
            || o.predicate != null
            || (o.families != null && !o.families.isEmpty())
            || (o.excludeFamilies != null && !o.excludeFamilies.isEmpty());
    }

    private static Predicate<Entity> andIf(Predicate<Entity> base, boolean cond, Predicate<Entity> extra) {
        return cond ? base.and(extra) : base;
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

        int cap = initialCap(o.limit);

        if (center != null) {
            cap = applyOrdering(list, center, o, cap);
        }

        if (cap != Integer.MAX_VALUE) {
            trimTo(list, cap);
        }
    }

    private static int initialCap(Integer limit) {
        return (limit != null && limit > 0) ? limit : Integer.MAX_VALUE;
    }

    private static boolean orderByClosest(EntityQueryOptions o) {
        return o.closest != null && o.closest > 0;
    }

    private static boolean orderByFarthest(EntityQueryOptions o) {
        return o.farthest != null && o.farthest > 0;
    }

    private static int applyOrdering(List<Entity> list, Vector3 center, EntityQueryOptions o, int cap) {
        if (orderByClosest(o)) {
            sortByDistanceAscending(list, center);
            return Math.min(cap, o.closest);
        }
        if (orderByFarthest(o)) {
            sortByDistanceDescending(list, center);
            return Math.min(cap, o.farthest);
        }
        return cap;
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
        return o != null
            && o.location != null
            && !hasOtherSpatialConstraints(o);
    }

    private static boolean hasOtherSpatialConstraints(EntityQueryOptions o) {
        return o.volume != null
            || o.maxDistance != null
            || o.minDistance != null
            || (o.closest != null && o.closest > 0)
            || (o.farthest != null && o.farthest > 0)
            || o.exactLocationMatch;
    }

    public static void filterByDistanceBand(List<Entity> list, Vector3 center, Double min, Double max) {
        if (list == null || list.isEmpty()) return;

        final double cx = center.x, cy = center.y, cz = center.z;
        final double minD2 = (min != null) ? (min * min) : -1.0;
        final double maxD2 = (max != null) ? (max * max) : -1.0;

        list.removeIf(e -> !withinDistanceBand(e, cx, cy, cz, minD2, maxD2));
    }

    public static boolean isAtBlock(Entity e, int x, int y, int z) {
        return e.getFloorX() == x && e.getFloorY() == y && e.getFloorZ() == z;
    }

    public static void collectExactBlockMatches(Map<Long, Entity> map, Vector3 loc, List<Entity> out) {
        if (map == null || map.isEmpty() || loc == null) return;

        int lx = NukkitMath.floorDouble(loc.x);
        int ly = NukkitMath.floorDouble(loc.y);
        int lz = NukkitMath.floorDouble(loc.z);

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

        return !(maxD2 >= 0.0 && d2 > maxD2) && !(minD2 >= 0.0 && d2 < minD2);
    }

    private static boolean insideCuboid(Entity e, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        return e.x >= minX && e.x <= maxX
            && e.y >= minY && e.y <= maxY
            && e.z >= minZ && e.z <= maxZ;
    }

    public static void collectEntitiesInChunks(
            Level level,
            ChunkQuery chunk,
            SpatialFilter filter,
            List<Entity> out
    ) {
        Predicate<Entity> p = buildChunkEntityPredicate(filter);
        forEachChunk(level, chunk, map -> addFiltered(map, p, out));
    }

    private static Predicate<Entity> buildChunkEntityPredicate(SpatialFilter f) {
        Predicate<Entity> p = e -> e != null;
        if (f == null) return p;

        if (f.minD2 >= 0.0 || f.maxD2 >= 0.0) {
            p = p.and(e -> withinDistanceBand(e, f.cx, f.cy, f.cz, f.minD2, f.maxD2));
        }
        if (f.useCuboid) {
            p = p.and(e -> insideCuboid(e, f.vMinX, f.vMaxX, f.vMinY, f.vMaxY, f.vMinZ, f.vMaxZ));
        }
        return p;
    }

    private static void addFiltered(Map<Long, Entity> map, Predicate<Entity> p, List<Entity> out) {
        if (map == null || map.isEmpty()) return;
        for (Entity e : map.values()) {
            if (p.test(e)) out.add(e);
        }
    }

    private static void forEachChunk(Level level,
                                     ChunkQuery chunk,
                                     Consumer<Map<Long, Entity>> consumer) {
        for (int cX = chunk.minCX; cX <= chunk.maxCX; ++cX) {
            for (int cZ = chunk.minCZ; cZ <= chunk.maxCZ; ++cZ) {
                consumer.accept(level.getChunkEntities(cX, cZ, chunk.loadChunks));
            }
        }
    }
}