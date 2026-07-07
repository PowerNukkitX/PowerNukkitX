package org.powernukkitx.command.selector.args.impl;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.exceptions.SelectorSyntaxException;
import org.powernukkitx.command.selector.ParseUtils;
import org.powernukkitx.command.selector.SelectorType;
import org.powernukkitx.command.selector.args.ISelectorArgument;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'r' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'r' argument is used to filter entities by their distance from a reference location, selecting only those
 * within a specified radius. This is commonly used in selectors such as @e[r=10] to select entities within 10 blocks
 * of the base position.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'r' selector argument for radius-based distance filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Parses the argument as a double and applies the filter to all entities.</li>
 *   <li>Returns a predicate that checks if an entity's distance squared to the base position is less than the square of the specified radius.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link ISelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[r=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[r=5] selects entities within 5 blocks of the base position
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 *   <li>Argument must be a valid double value.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see ISelectorArgument
 * @see ParseUtils
 * @see SelectorType
 * @see CommandSender
 * @see Location
 * @see Entity
 * @since PowerNukkitX 2.0.0
 */
public class R implements ISelectorArgument {
    @Override
    public Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var r = Double.parseDouble(arguments[0]);
        return entity -> entity.distanceSquared(basePos) < Math.pow(r, 2);
    }

    /**
     * Returns the key name for this selector argument ('r').
     *
     * @return the string "r"
     */
    @Override
    public String getKeyName() {
        return "r";
    }

    /**
     * Returns the priority for this argument (3).
     *
     * @return the integer 3
     */
    @Override
    public int getPriority() {
        return 3;
    }
}
