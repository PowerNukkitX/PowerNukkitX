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
 * Selector argument implementation for the 'ry' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'ry' argument is used to filter entities by their yaw (horizontal rotation), selecting only those
 * whose yaw is less than or equal to the specified value. The value must be within the range -180 to 180.
 * This is commonly used in selectors such as @e[ry=90] to select entities facing no more than 90 degrees east of south.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'ry' selector argument for yaw-based filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Checks that the argument is within the valid yaw range (-180 to 180).</li>
 *   <li>Returns a predicate that checks if an entity's yaw (converted to vanilla coordinate system) is less than or equal to the specified value.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for out-of-bounds or invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[ry=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[ry=90] selects entities with yaw less than or equal to 90
 * // @e[ry=-45] selects entities with yaw less than or equal to -45
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
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class RY extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var ry = Double.parseDouble(arguments[0]);
        if (!ParseUtils.checkBetween(-180d, 180d, ry))
            throw new SelectorSyntaxException("RX out of bound (-180 - 180): " + ry);
        // The internal yaw range is [0, 360], vanilla uses [-180, 180]. Convert accordingly.
        // Also aligns +z with south to match vanilla selector behavior.
        return entity -> ((entity.getYaw() + 90) % 360 - 180) <= ry;
    }

    @Override
    public String getKeyName() {
        return "ry";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
