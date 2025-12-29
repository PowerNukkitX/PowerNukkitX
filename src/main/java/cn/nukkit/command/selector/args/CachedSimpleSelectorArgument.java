package cn.nukkit.command.selector.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Abstract base class for selector arguments that support caching of parsed predicates in PowerNukkitX.
 * <p>
 * This class is designed for selector arguments whose predicate results are not time-sensitive and can be reused
 * for the same argument set. It provides a built-in cache (using Caffeine) to store and retrieve {@link Predicate<Entity>}
 * instances based on the argument set, improving performance for repeated selector queries.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Provides a Caffeine-based cache for mapping argument sets to parsed predicates.</li>
 *   <li>Automatically retrieves cached predicates or computes and stores them if not present.</li>
 *   <li>Abstracts the predicate parsing logic via {@link #cache(SelectorType, CommandSender, Location, String...)}.</li>
 *   <li>Allows customization of the cache implementation by overriding {@link #provideCacheService()}.</li>
 *   <li>Integrates with the {@link ISelectorArgument} interface for use in the entity selector system.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Extend this class to implement a selector argument whose predicate can be cached for argument sets.</li>
 *   <li>Implement {@link #cache(SelectorType, CommandSender, Location, String...)} to provide the predicate logic.</li>
 *   <li>Optionally override {@link #provideCacheService()} to customize cache size, expiration, etc.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class TypeArgument extends CachedSimpleSelectorArgument {
 *     @Override
 *     protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
 *         String type = arguments[0];
 *         return entity -> entity.getName().equalsIgnoreCase(type);
 *     }
 * }
 * </pre>
 *
 * <b>Thread Safety:</b> The cache is thread-safe for concurrent access.
 *
 * @author PowerNukkitX Project Team
 * @see ISelectorArgument
 * @see com.github.benmanes.caffeine.cache.Cache
 * @see Predicate
 * @since PowerNukkitX 2.0.0
 */


public abstract class CachedSimpleSelectorArgument implements ISelectorArgument {

    Cache<Set<String>, Predicate<Entity>> cache;

    public CachedSimpleSelectorArgument() {
        this.cache = provideCacheService();
    }

    /**
     * Returns a predicate for the given selector arguments, using the cache if available.
     * <p>
     * If the predicate for the argument set is not cached, calls {@link #cache(SelectorType, CommandSender, Location, String...)}
     * to compute and store it.
     *
     * @param selectorType the selector type
     * @param sender the command sender
     * @param basePos the reference location
     * @param arguments the argument values
     * @return the cached or computed predicate
     * @throws SelectorSyntaxException if parsing fails
     */
    @Override
    public Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        var value = cache.getIfPresent(Sets.newHashSet(arguments));
        if (value == null) {
            value = cache(selectorType, sender, basePos, arguments);
            cache.put(Sets.newHashSet(arguments), value);
        }
        return value;
    }

    /**
     * Computes the predicate for the given selector arguments when not found in the cache.
     * <p>
     * Subclasses must implement this method to provide the predicate logic for the argument set.
     *
     * @param selectorType the selector type
     * @param sender the command sender
     * @param basePos the reference location
     * @param arguments the argument values
     * @return the computed predicate
     * @throws SelectorSyntaxException if parsing fails
     */
    protected abstract Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException;

    /**
     * Provides the cache instance for storing argument set to predicate mappings.
     * <p>
     * Subclasses may override this method to customize cache size, expiration, or implementation.
     *
     * @return the cache instance
     */
    protected Cache<Set<String>, Predicate<Entity>> provideCacheService() {
        return Caffeine.newBuilder().maximumSize(65535).expireAfterAccess(1, TimeUnit.MINUTES).build();
    }
}