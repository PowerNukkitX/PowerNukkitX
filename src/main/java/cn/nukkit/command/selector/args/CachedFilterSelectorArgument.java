package cn.nukkit.command.selector.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Abstract base class for selector arguments that support caching of filter functions in PowerNukkitX.
 * <p>
 * This class is designed for selector arguments that operate in filter mode (i.e., process the entity list as a whole)
 * and whose filter results are not time-sensitive, allowing reuse for the same argument set. It provides a built-in
 * cache (using Caffeine) to store and retrieve {@link Function} instances mapping argument sets to filtered entity lists.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Provides a Caffeine-based cache for mapping argument sets to filter functions.</li>
 *   <li>Automatically retrieves cached filter functions or computes and stores them if not present.</li>
 *   <li>Abstracts the filter logic via {@link #cache(SelectorType, CommandSender, Location, String...)}.</li>
 *   <li>Always operates in filter mode (overrides {@link ISelectorArgument#isFilter()} to return true).</li>
 *   <li>Allows customization of the cache implementation by overriding {@link #provideCacheService()}.</li>
 *   <li>Integrates with the {@link ISelectorArgument} interface for use in the entity selector system.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Extend this class to implement a selector argument whose filter function can be cached for argument sets.</li>
 *   <li>Implement {@link #cache(SelectorType, CommandSender, Location, String...)} to provide the filter logic.</li>
 *   <li>Optionally override {@link #provideCacheService()} to customize cache size, expiration, etc.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class LimitArgument extends CachedFilterSelectorArgument {
 *     @Override
 *     protected Function<List<Entity>, List<Entity>> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
 *         int limit = Integer.parseInt(arguments[0]);
 *         return entities -> entities.stream().limit(limit).toList();
 *     }
 * }
 * </pre>
 *
 * <b>Thread Safety:</b> The cache is thread-safe for concurrent access.
 *
 * @author PowerNukkitX Project Team
 * @see ISelectorArgument
 * @see com.github.benmanes.caffeine.cache.Cache
 * @see Function
 * @since PowerNukkitX 2.0.0
 */


public abstract class CachedFilterSelectorArgument implements ISelectorArgument {

    Cache<Set<String>, Function<List<Entity>, List<Entity>>> cache;

    public CachedFilterSelectorArgument() {
        this.cache = provideCacheService();
    }

    /**
     * Returns a filter function for the given selector arguments, using the cache if available.
     * <p>
     * If the filter function for the argument set is not cached, calls {@link #cache(SelectorType, CommandSender, Location, String...)}
     * to compute and store it.
     *
     * @param selectorType the selector type
     * @param sender the command sender
     * @param basePos the reference location
     * @param arguments the argument values
     * @return the cached or computed filter function
     * @throws SelectorSyntaxException if parsing fails
     */
    @Override
    public Function<List<Entity>, List<Entity>> getFilter(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        var value = cache.getIfPresent(Sets.newHashSet(arguments));
        if (value == null) {
            value = cache(selectorType, sender, basePos, arguments);
            cache.put(Sets.newHashSet(arguments), value);
        }
        return value;
    }

    /**
     * Always returns true, indicating this argument operates in filter mode.
     *
     * @return true
     */
    @Override
    public boolean isFilter() {
        return true;
    }

    /**
     * Computes the filter function for the given selector arguments when not found in the cache.
     * <p>
     * Subclasses must implement this method to provide the filter logic for the argument set.
     *
     * @param selectorType the selector type
     * @param sender the command sender
     * @param basePos the reference location
     * @param arguments the argument values
     * @return the computed filter function
     * @throws SelectorSyntaxException if parsing fails
     */
    protected abstract Function<List<Entity>, List<Entity>> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException;

    /**
     * Provides the cache instance for storing argument set to filter function mappings.
     * <p>
     * Subclasses may override this method to customize cache size, expiration, or implementation.
     *
     * @return the cache instance
     */
    protected Cache<Set<String>, Function<List<Entity>, List<Entity>>> provideCacheService() {
        return Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();
    }
}
