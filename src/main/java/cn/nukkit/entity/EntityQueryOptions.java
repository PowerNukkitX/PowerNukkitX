package cn.nukkit.entity;

import cn.nukkit.math.Vector3;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


/**
 * Options for filtering and sorting results from {@code Level#getEntities(...)}.
 *
 * <p>Combine position, distance, type, tags, and custom predicates to refine search results.
 * All filters are ANDed. If no spatial constraint is set, only loaded entities are scanned.</p>
 *
 * <p><b>Spatial filters:</b></p>
 * <ul>
 *     <li><b>location</b> – Center point for distance/volume filters.</li>
 *     <li><b>maxDistance</b> – Max spherical distance from location (requires location).</li>
 *     <li><b>minDistance</b> – Min spherical distance from location (requires location).</li>
 *     <li><b>volume</b> – Cuboid width/height/depth centered at location, useful for axis-aligned scans.</li>
 *     <li><b>margin</b> – Extra blocks to avoid missing large mobs crossing chunk borders (e.g., ghasts, bosses).</li>
 *     <li><b>exactLocationMatch</b> – Only match entities exactly at block coords of location (ignores hitbox size).</li>
 * </ul>
 *
 * <p><b>Result limits and ordering:</b></p>
 * <ul>
 *     <li><b>limit</b> – Max results after filtering/sorting. Handy for performance-sensitive scans.</li>
 *     <li><b>closest</b> – Keep N closest entities (requires location).</li>
 *     <li><b>farthest</b> – Keep N farthest entities (requires location).</li>
 * </ul>
 *
 * <p><b>Entity filters:</b></p>
 * <ul>
 *     <li><b>tags</b> – Required tags; entity must have all. Matches {@code Entity.hasTag(...)}.</li>
 *     <li><b>excludeTags</b> – Tags that must not be present.</li>
 *     <li><b>typeClass</b> – Match only a given entity class/subclass (e.g., {@code EntityZombie.class}).</li>
 *     <li><b>nameTagEquals</b> – Exact case-sensitive match for {@code Entity.getNameTag()}.</li>
 *     <li><b>predicate</b> – Java predicate filter for any complex logic.</li>
 * </ul>
 *
 * <p><b>Chunk loading:</b></p>
 * <ul>
 *     <li><b>loadChunks</b> – If true, loads chunks in the search area. Default: false. 
 *     Use with care to avoid lag spikes.</li>
 * </ul>
 *
 * <p><b>Factory methods:</b></p>
 * <ul>
 *     <li>{@link #of(Vector3, double)} – Quick center + radius query.</li>
 *     <li>{@link #of(Vector3, double, boolean)} – Same, but can load chunks.</li>
 * </ul>
 *
 * <p><b>Example:</b> find the 10 closest zombies within 50 blocks:</p>
 * <pre>
 *     List<Entity> zombies = level.getEntities(
 *         new EntityQueryOptions()
 *             .location(player.getPosition())
 *             .maxDistance(50)
 *             .typeClass(EntityZombie.class)
 *             .closest(10)
 *     );
 * </pre>
 */
public final class EntityQueryOptions {
    public Vector3 location;
    public Double maxDistance;
    public Double minDistance;
    public Vector3 volume;

    public Integer limit;
    public Integer closest;
    public Integer farthest;

    public Set<String> tags;
    public Set<String> excludeTags;
    public Class<? extends Entity> typeClass;
    public Set<String> families;
    public Set<String> excludeFamilies;
    public String nameTagEquals;

    public Predicate<Entity> predicate;

    public boolean loadChunks = false;
    public double margin = 3.5;
    public boolean exactLocationMatch = false;

    /**
     * Sets the center location of the entity search.
     * <p>
     * Used as the reference for distance, cuboid volume,
     * closest/farthest sorting, and exact-location match.
     *
     * @param location The {@link Vector3} to center the query on.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions location(Vector3 location) {
        this.location = location;
        return this;
    }

    /**
     * Sets the maximum spherical distance (radius) from {@link #location}.
     * <p>
     * Requires {@link #location(Vector3)}.
     *
     * @param maxDistance Distance in blocks.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions maxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    /**
     * Sets the minimum spherical distance (radius) from {@link #location}.
     * <p>
     * Requires {@link #location(Vector3)}.
     *
     * @param minDistance Distance in blocks.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions minDistance(double minDistance) {
        this.minDistance = minDistance;
        return this;
    }

    /**
     * Sets a cuboid volume (width, height, depth) starting at {@link #location}, extending toward the positive axes (+X, +Y, +Z).
     * <p>
     * Example: {@code new Vector3(10, 5, 10)} scans a 10×5×10 box.
     * Requires {@link #location(Vector3)}.
     *
     * @param volume The dimensions in blocks.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions volume(Vector3 volume) {
        this.volume = volume;
        return this;
    }

    /**
     * Limits the number of entities returned (after filtering/sorting).
     *
     * @param limit Max result count.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Keeps only the N closest entities to {@link #location}.
     * <p>
     * Requires {@link #location(Vector3)}.
     *
     * @param closest Number to keep.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions closest(int closest) {
        this.closest = closest;
        return this;
    }

    /**
     * Keeps only the N farthest entities from {@link #location}.
     * <p>
     * Requires {@link #location(Vector3)}.
     *
     * @param farthest Number to keep.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions farthest(int farthest) {
        this.farthest = farthest;
        return this;
    }

    /**
     * Requires that entities contain all given tags.
     *
     * @param tags One or more tag strings.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions tags(String... tags) {
        this.tags = new HashSet<>(Arrays.asList(tags));
        return this;
    }

    /**
     * Requires that entities contain none of the given tags.
     *
     * @param excludeTags One or more tag strings to exclude.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions excludeTags(String... excludeTags) {
        this.excludeTags = new HashSet<>(Arrays.asList(excludeTags));
        return this;
    }

    /**
     * Matches entities by Java class (subclasses allowed).
     * <p>
     * Example: {@code typeClass(EntityZombie.class)}.
     *
     * @param typeClass The class to match.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions typeClass(Class<? extends Entity> typeClass) {
        this.typeClass = typeClass;
        return this;
    }

    /**
     * Exact, case-sensitive match for {@code Entity.getNameTag()}.
     *
     * @param nameTagEquals The name tag to match.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions nameTagEquals(String nameTagEquals) {
        this.nameTagEquals = nameTagEquals;
        return this;
    }

    /**
     * Matches entities by family type (ALL-of).
     * <p>
     * Example: {@code families("monster", "npc")}.
     *
     * @param families The family to match, can be a single string or multiple string, if multiple the entity must match all of them..
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions families(Collection<String> families) {
        this.families = (families == null || families.isEmpty()) ? null : Set.copyOf(families);
        return this;
    }

    /**
     * Matches entities by family type (ALL-of).
     * <p>
     * Example: {@code families("monster", "npc")}.
     *
     * @param families The family to match, can be a single string or multiple string, if multiple the entity must match all of them..
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions families(String... families) {
        this.families = (families == null || families.length == 0) ? null : Set.of(families);
        return this;
    }

    /**
     * Excludes entities by family type (ANY-of).
     * <p>
     * Example: {@code excludeFamilies("monster", "npc")}.
     *
     * @param excludeFamilies The family to match, can be a single string or multiple string, if multiple the entity must match all of them..
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions excludeFamilies(Collection<String> excludeFamilies) {
        this.excludeFamilies = (excludeFamilies == null || excludeFamilies.isEmpty()) ? null : Set.copyOf(excludeFamilies);
        return this;
    }

    /**
     * Excludes entities by family type (ANY-of).
     * <p>
     * Example: {@code excludeFamilies("monster", "npc")}.
     *
     * @param excludeFamilies The family to match, can be a single string or multiple string, if multiple the entity must match all of them..
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions excludeFamilies(String... excludeFamilies) {
        this.excludeFamilies = (excludeFamilies == null || excludeFamilies.length == 0) ? null : Set.of(excludeFamilies);
        return this;
    }

    /**
     * Applies a custom predicate after spatial filters.
     *
     * @param predicate Returns true to include the entity.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions predicate(Predicate<Entity> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Whether to load chunks that are not loaded.
     * <p>
     * Default: false (unloaded chunks are skipped).
     *
     * @param loadChunks True to load on demand.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions loadChunks(boolean loadChunks) {
        this.loadChunks = loadChunks;
        return this;
    }

    /**
     * Expands the scan window slightly to account for large hitboxes crossing chunk borders.
     *
     * @param margin Extra distance in blocks.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions margin(double margin) {
        this.margin = margin;
        return this;
    }

    /**
     * Only match entities whose block coordinates exactly equal {@link #location}.
     * Ignores hitbox size and neighbors.
     *
     * @param exactLocationMatch True to enable exact block match.
     * @return This {@link EntityQueryOptions} for chaining.
     */
    public EntityQueryOptions exactLocationMatch(boolean exactLocationMatch) {
        this.exactLocationMatch = exactLocationMatch;
        return this;
    }


    /**
     * Convenience: center + radius.
     *
     * @param center Center location.
     * @param radius Spherical radius in blocks.
     * @return New {@link EntityQueryOptions}.
     */
    public static EntityQueryOptions of(Vector3 center, double radius) {
        return new EntityQueryOptions()
                .location(center)
                .maxDistance(radius);
    }

    /**
     * Convenience: center + radius + optional chunk loading.
     *
     * @param center Center location.
     * @param radius Spherical radius in blocks.
     * @param loadChunks Whether to load chunks.
     * @return New {@link EntityQueryOptions}.
     */
    public static EntityQueryOptions of(Vector3 center, double radius, boolean loadChunks) {
        return new EntityQueryOptions()
                .location(center)
                .maxDistance(radius)
                .loadChunks(loadChunks);
    }
}