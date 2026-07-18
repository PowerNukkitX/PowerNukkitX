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
 * Selector argument implementation for the 'rxm' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'rxm' argument is used to filter entities by their minimum pitch (vertical rotation), selecting only those
 * whose pitch is greater than or equal to the specified value. The value must be within the range -90 to 90.
 * This is commonly used in selectors such as @e[rxm=-45] to select entities looking down at least -45 degrees.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'rxm' selector argument for minimum pitch-based filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Checks that the argument is within the valid pitch range (-90 to 90).</li>
 *   <li>Returns a predicate that checks if an entity's pitch is greater than or equal to the specified value.</li>
 *   <li>Throws {@link org.powernukkitx.command.exceptions.SelectorSyntaxException} for out-of-bounds or invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[rxm=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[rxm=30] selects entities with pitch greater than or equal to 30
 * // @e[rxm=-45] selects entities with pitch greater than or equal to -45
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 *   <li>Argument must be a double between -90 and 90 (inclusive).</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CachedSimpleSelectorArgument
 * @see org.powernukkitx.command.selector.ParseUtils
 * @see org.powernukkitx.command.selector.SelectorType
 * @see org.powernukkitx.command.CommandSender
 * @see org.powernukkitx.level.Location
 * @see org.powernukkitx.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class RXM extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var rxm = Double.parseDouble(arguments[0]);
        if (!ParseUtils.checkBetween(-90d, 90d, rxm))
            throw new SelectorSyntaxException("RXM out of bound (-90 - 90): " + rxm);
        return entity -> entity.getPitch() >= rxm;
    }

    @Override
    public String getKeyName() {
        return "rxm";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
