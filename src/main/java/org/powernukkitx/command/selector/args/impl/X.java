package org.powernukkitx.command.selector.args.impl;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.exceptions.SelectorSyntaxException;
import org.powernukkitx.command.selector.ParseUtils;
import org.powernukkitx.command.selector.SelectorType;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'x' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'x' argument is used to specify the X coordinate reference for entity selection. It allows selectors to define
 * the base X position for range-based arguments (such as dx) and for filtering entities within a specific area.
 * This argument updates the reference location's X value for subsequent range checks.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'x' selector argument for coordinate-based entity selection.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Parses the argument as an absolute or relative double value (e.g., 100, ~5, ~).</li>
 *   <li>Updates the base position's X coordinate for use by other arguments (e.g., dx).</li>
 *   <li>Does not return a predicate; only modifies the reference location.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CoordinateArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[x=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[x=100] sets the base X coordinate to 100 for range-based selection
 * // @e[x=~5] sets the base X coordinate to 5 blocks relative to the sender's position
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 *   <li>Argument must be a valid double value (absolute or relative).</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CoordinateArgument
 * @see org.powernukkitx.command.selector.ParseUtils
 * @see org.powernukkitx.command.selector.SelectorType
 * @see org.powernukkitx.command.CommandSender
 * @see org.powernukkitx.level.Location
 * @see org.powernukkitx.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class X extends CoordinateArgument {
    @Override
    public Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        basePos.setX(ParseUtils.parseOffsetDouble(arguments[0], basePos.getX()));
        return null;
    }

    @Override
    public String getKeyName() {
        return "x";
    }
}
