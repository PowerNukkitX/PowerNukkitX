package org.powernukkitx.command.selector.args.impl;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.exceptions.SelectorSyntaxException;
import org.powernukkitx.command.selector.ParseUtils;
import org.powernukkitx.command.selector.SelectorType;
import org.powernukkitx.command.selector.args.CachedFilterSelectorArgument;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Selector argument implementation for the 'c' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'c' argument is used to limit the number of entities returned by a selector, optionally reversing the order
 * to select the farthest entities instead of the nearest. This class sorts the entity list by distance to the
 * reference location and returns up to the specified number of entities. Negative values reverse the order.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'c' selector argument for entity count limiting.</li>
 *   <li>Sorts entities by distance to the base position (nearest first).</li>
 *   <li>Negative values reverse the order (farthest first).</li>
 *   <li>Throws {@link org.powernukkitx.command.exceptions.SelectorSyntaxException} for zero or invalid values.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link org.powernukkitx.command.selector.args.CachedFilterSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[c=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[c=3] selects the 3 nearest entities
 * // @e[c=-2] selects the 2 farthest entities
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 *   <li>Argument must not be zero (throws if zero).</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see org.powernukkitx.command.selector.args.CachedFilterSelectorArgument
 * @see org.powernukkitx.command.selector.ParseUtils
 * @see org.powernukkitx.command.selector.SelectorType
 * @see org.powernukkitx.command.CommandSender
 * @see org.powernukkitx.level.Location
 * @see org.powernukkitx.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class C extends CachedFilterSelectorArgument {
    /**
     * Parses the 'c' argument and returns a filter function that limits the entity list.
     * <p>
     * Sorts entities by distance to the base position. If the argument is negative, reverses the order.
     *
     * @param selectorType the selector type
     * @param sender the command sender
     * @param basePos the reference location
     * @param arguments the argument values (should contain one integer as a string)
     * @return a function that limits the entity list to the specified count
     * @throws SelectorSyntaxException if the argument is invalid
     */
    @Override
    public Function<List<Entity>, List<Entity>> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var c = Integer.parseInt(arguments[0]);
        if (c == 0) throw new SelectorSyntaxException("C cannot be zero!");
        final Location referencePos = basePos.clone();
        return entities -> {
            if (selectorType == SelectorType.RANDOM_PLAYER) {
                Collections.shuffle(entities, ThreadLocalRandom.current());
            } else {
                entities.sort(Comparator.comparingDouble(e -> e.distanceSquared(referencePos)));
                if (c < 0)
                    Collections.reverse(entities);
            }

            int limit = (int) Math.min(Math.abs((long) c), entities.size());
            return new ArrayList<>(entities.subList(0, limit));
        };
    }

    /**
     * Returns the filter function for the given arguments.
     * <p>
     * The filter is position dependent, so it is rebuilt for every sender instead of being served from the
     * shared argument cache.
     *
     * @param selectorType the selector type
     * @param sender the command sender
     * @param basePos the reference location
     * @param arguments the argument values
     * @return the computed filter function
     * @throws SelectorSyntaxException if the argument is invalid
     */
    @Override
    public Function<List<Entity>, List<Entity>> getFilter(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        return cache(selectorType, sender, basePos, arguments);
    }

    /**
     * Returns the key name for this selector argument ('c').
     *
     * @return the string "c"
     */
    @Override
    public String getKeyName() {
        return "c";
    }

    /**
     * Returns the priority for this argument.
     * The count limit is applied after all other selector filters.
     *
     * @return the integer 1000
     */
    @Override
    public int getPriority() {
        return 1000;
    }
}
