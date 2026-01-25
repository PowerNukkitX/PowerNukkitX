package cn.nukkit.command.selector.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Defines the contract for a selector argument used in Minecraft command selectors in PowerNukkitX.
 * <p>
 * Implementations of this interface provide logic for parsing, filtering, and matching entities based on selector arguments
 * (e.g., type, name, tag, coordinates) in advanced command selector syntax (such as @e[type=zombie]).
 * Selector arguments can be used as predicates (per-entity checks) or as filters (batch entity processing),
 * and support custom parsing, default values, and priority-based evaluation order.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Defines a key name for the argument (e.g., "type", "x", "tag").</li>
 *   <li>Supports predicate-based filtering via {@link #getPredicate(SelectorType, CommandSender, Location, String...)}.</li>
 *   <li>Supports filter-based processing via {@link #getFilter(SelectorType, CommandSender, Location, String...)} for batch entity filtering.</li>
 *   <li>Allows specifying a default value for the argument if not present in the selector.</li>
 *   <li>Supports priority-based evaluation to control argument parsing order.</li>
 *   <li>Enables filter mode for arguments that operate on entity lists rather than single entities.</li>
 *   <li>Extends {@link Comparable} for consistent ordering in selector parsing.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Implement this interface to define a custom selector argument for use in {@link cn.nukkit.command.selector.EntitySelectorAPI}.</li>
 *   <li>Override {@link #getPredicate} for per-entity checks, or {@link #getFilter} for batch filtering.</li>
 *   <li>Set the argument's priority to control its evaluation order (lower values are evaluated first).</li>
 *   <li>Return a default value from {@link #getDefaultValue} if the argument should be used when not explicitly specified.</li>
 *   <li>Use {@link #isFilter()} to enable filter mode for arguments that process entity lists.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class TypeArgument implements ISelectorArgument {
 *     public String getKeyName() { return "type"; }
 *     public int getPriority() { return 10; }
 *     public Predicate<Entity> getPredicate(SelectorType type, CommandSender sender, Location base, String... args) {
 *         String entityType = args[0];
 *         return entity -> entity.getName().equalsIgnoreCase(entityType);
 *     }
 * }
 * </pre>
 *
 * <b>Filter Mode:</b> If {@link #isFilter()} returns true, {@link #getFilter} is called instead of {@link #getPredicate}.
 * This allows arguments to process or limit the entity list as a whole (e.g., limit count, sort, etc.).
 *
 * <b>Thread Safety:</b> Implementations should be stateless or thread-safe for concurrent command execution.
 *
 * @author PowerNukkitX Project Team
 * @see cn.nukkit.command.selector.EntitySelectorAPI
 * @see SelectorType
 * @see CommandSender
 * @see Entity
 * @see Location
 * @see Predicate
 * @see Function
 * @since PowerNukkitX 2.0.0
 */
public interface ISelectorArgument extends Comparable<ISelectorArgument> {
    /**
     * Returns a predicate for filtering entities based on the selector argument and its values.
     * <p>
     * This method is called for arguments that are not in filter mode. The predicate is applied to each entity
     * individually to determine if it matches the selector criteria.
     *
     * @param selectorType the selector type (e.g., @e, @p, @a)
     * @param sender the command sender
     * @param basePos the reference location for relative arguments (may be modified by coordinate arguments)
     * @param arguments the argument values for this selector argument
     * @return a predicate for entity filtering, or null if not applicable
     * @throws SelectorSyntaxException if parsing fails or arguments are invalid
     */
    @Nullable
    default Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        return null;
    }

    /**
     * Returns a filter function for processing the entity list as a whole.
     * <p>
     * This method is called only if {@link #isFilter()} returns true. The filter function receives the current entity list
     * and returns a new list after applying the argument's logic (e.g., limiting count, sorting, etc.).
     *
     * @param selectorType the selector type (e.g., @e, @p, @a)
     * @param sender the command sender
     * @param basePos the reference location for relative arguments (may be modified by coordinate arguments)
     * @param arguments the argument values for this selector argument
     * @return a function that processes and returns a filtered entity list, or null if not applicable
     * @throws SelectorSyntaxException if parsing fails or arguments are invalid
     */
    default Function<List<Entity>, List<Entity>> getFilter(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        return null;
    }

    /**
     * Returns the key name of this selector argument (e.g., "type", "x", "tag").
     *
     * @return the argument key name
     */
    String getKeyName();

    /**
     * Returns the priority of this argument for evaluation order.
     * <p>
     * Lower values are evaluated first. Priority determines the order in which arguments are parsed and applied.
     *
     * @return the argument's evaluation priority (lower = higher priority)
     */
    int getPriority();

    /**
     * Indicates whether this argument operates in filter mode.
     * <p>
     * If true, {@link #getFilter} is called instead of {@link #getPredicate} and the argument processes the entity list as a whole.
     *
     * @return true if filter mode is enabled, false otherwise
     */
    default boolean isFilter() {
        return false;
    }

    /**
     * Returns the default value for this argument if not present in the selector.
     * <p>
     * If this method returns a non-null value, it will be used as the argument value when the selector does not specify it.
     *
     * @param values the full argument map for the selector
     * @param selectorType the selector type
     * @param sender the command sender
     * @return the default value for this argument, or null if none
     */
    @Nullable
    default String getDefaultValue(Map<String, List<String>> values, SelectorType selectorType, CommandSender sender) {
        return null;
    }

    /**
     * Compares this argument to another for ordering by priority.
     * <p>
     * Arguments with lower priority values are ordered before those with higher values.
     *
     * @param o the other selector argument
     * @return a negative integer, zero, or a positive integer as this argument has less than, equal to, or greater priority
     */
    @Override
    default int compareTo(@NotNull ISelectorArgument o) {
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
