package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'y' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'y' argument is used to specify the Y coordinate reference for entity selection. It allows selectors to define
 * the base Y position for range-based arguments (such as dy) and for filtering entities within a specific area.
 * This argument updates the reference location's Y value for subsequent range checks.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'y' selector argument for coordinate-based entity selection.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Parses the argument as an absolute or relative double value (e.g., 64, ~5, ~).</li>
 *   <li>Updates the base position's Y coordinate for use by other arguments (e.g., dy).</li>
 *   <li>Does not return a predicate; only modifies the reference location.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CoordinateArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[y=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[y=64] sets the base Y coordinate to 64 for range-based selection
 * // @e[y=~5] sets the base Y coordinate to 5 blocks relative to the sender's position
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
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class Y extends CoordinateArgument {
    @Override
    public Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        basePos.setY(ParseUtils.parseOffsetDouble(arguments[0], basePos.getY()));
        return null;
    }

    @Override
    public String getKeyName() {
        return "y";
    }
}
