package org.powernukkitx.command.selector.args.impl;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.exceptions.SelectorSyntaxException;
import org.powernukkitx.command.selector.ParseUtils;
import org.powernukkitx.command.selector.SelectorType;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'dy' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'dy' argument is used to define a range along the Y-axis for entity selection. It filters entities whose Y coordinate
 * falls within the range starting from the base position's Y and extending by the specified dy value. This is commonly used
 * in selectors such as @e[dy=10] to select entities within a 10-block range along the Y-axis from the reference point.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'dy' selector argument for Y-axis range filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Calculates the range using the base position's Y and the dy value.</li>
 *   <li>Returns a predicate that checks if an entity's Y coordinate is within the specified range.</li>
 *   <li>Throws {@link org.powernukkitx.command.exceptions.SelectorSyntaxException} for invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link ScopeArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[dy=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[dy=5] selects entities within 5 blocks along the Y-axis from the base position
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see ScopeArgument
 * @see org.powernukkitx.command.selector.ParseUtils
 * @see org.powernukkitx.command.selector.SelectorType
 * @see org.powernukkitx.command.CommandSender
 * @see org.powernukkitx.level.Location
 * @see org.powernukkitx.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class DY extends ScopeArgument {
    @Override
    public @Nullable Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        var y = basePos.getY();
        var dy = Double.parseDouble(arguments[0]);
        return entity -> ParseUtils.checkBetween(y, y + dy, entity.getY());
    }

    /**
     * Returns the key name for this selector argument ('dy').
     *
     * @return the string "dy"
     */
    @Override
    public String getKeyName() {
        return "dy";
    }
}
