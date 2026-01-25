package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'dx' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'dx' argument is used to define a range along the X-axis for entity selection. It filters entities whose X coordinate
 * falls within the range starting from the base position's X and extending by the specified dx value. This is commonly used
 * in selectors such as @e[dx=10] to select entities within a 10-block range along the X-axis from the reference point.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'dx' selector argument for X-axis range filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Calculates the range using the base position's X and the dx value.</li>
 *   <li>Returns a predicate that checks if an entity's X coordinate is within the specified range.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link ScopeArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[dx=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[dx=5] selects entities within 5 blocks along the X-axis from the base position
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
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @since PowerNukkitX 2.0.0
 */
public class DX extends ScopeArgument {
    @Override
    public @Nullable Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        var x = basePos.getX();
        var dx = Double.parseDouble(arguments[0]);
        return entity -> ParseUtils.checkBetween(x, x + dx, entity.getX());
    }

    /**
     * Returns the key name for this selector argument ('dx').
     *
     * @return the string "dx"
     */
    @Override
    public String getKeyName() {
        return "dx";
    }
}
