package org.powernukkitx.command.selector.args.impl;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.exceptions.SelectorSyntaxException;
import org.powernukkitx.command.selector.ParseUtils;
import org.powernukkitx.command.selector.SelectorType;
import org.powernukkitx.command.selector.args.CachedSimpleSelectorArgument;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'rym' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'rym' argument is used to filter entities by their minimum yaw (horizontal rotation), selecting only those
 * whose yaw is greater than or equal to the specified value. The value must be within the range -180 to 180.
 * This is commonly used in selectors such as @e[rym=-45] to select entities facing at least -45 degrees east of south.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'rym' selector argument for minimum yaw-based filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Checks that the argument is within the valid yaw range (-180 to 180).</li>
 *   <li>Returns a predicate that checks if an entity's yaw (converted to vanilla coordinate system) is greater than or equal to the specified value.</li>
 *   <li>Throws {@link SelectorSyntaxException} for out-of-bounds or invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[rym=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[rym=90] selects entities with yaw greater than or equal to 90
 * // @e[rym=-45] selects entities with yaw greater than or equal to -45
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 *   <li>Argument must be a double between -180 and 180 (inclusive).</li>
 * </ul>
 *
 * <b>Yaw Conversion:</b>
 * <ul>
 *   <li>The internal yaw range is [0, 360], but vanilla Minecraft uses [-180, 180].</li>
 *   <li>The code converts the yaw to vanilla's coordinate system: <code>((entity.getYaw() + 90) % 360 - 180)</code>.</li>
 *   <li>This aligns +z with south, matching vanilla selector behavior.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CachedSimpleSelectorArgument
 * @see ParseUtils
 * @see SelectorType
 * @see CommandSender
 * @see Location
 * @see Entity
 * @since PowerNukkitX 2.0.0
 */
public class RYM extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var rym = Double.parseDouble(arguments[0]);
        if (!ParseUtils.checkBetween(-180d, 180d, rym))
            throw new SelectorSyntaxException("RX out of bound (-180 - 180): " + rym);
        return entity -> ((entity.getYaw() + 90) % 360 - 180) >= rym;
    }

    @Override
    public String getKeyName() {
        return "rym";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
