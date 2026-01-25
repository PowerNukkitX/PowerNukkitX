package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'rx' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'rx' argument is used to filter entities by their pitch (vertical rotation), selecting only those
 * whose pitch is less than or equal to the specified value. The value must be within the range -90 to 90.
 * This is commonly used in selectors such as @e[rx=45] to select entities looking up no more than 45 degrees.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'rx' selector argument for pitch-based filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Checks that the argument is within the valid pitch range (-90 to 90).</li>
 *   <li>Returns a predicate that checks if an entity's pitch is less than or equal to the specified value.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for out-of-bounds or invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[rx=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[rx=30] selects entities with pitch less than or equal to 30
 * // @e[rx=-45] selects entities with pitch less than or equal to -45
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
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class RX extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var rx = Double.parseDouble(arguments[0]);
        if (!ParseUtils.checkBetween(-90d, 90d, rx))
            throw new SelectorSyntaxException("RX out of bound (-90 - 90): " + rx);
        return entity -> entity.getPitch() <= rx;
    }

    @Override
    public String getKeyName() {
        return "rx";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
