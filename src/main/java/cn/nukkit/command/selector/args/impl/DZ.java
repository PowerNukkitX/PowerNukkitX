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
 * Selector argument implementation for the 'dz' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'dz' argument is used to define a range along the Z-axis for entity selection. It filters entities whose Z coordinate
 * falls within the range starting from the base position's Z and extending by the specified dz value. This is commonly used
 * in selectors such as @e[dz=10] to select entities within a 10-block range along the Z-axis from the reference point.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'dz' selector argument for Z-axis range filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Calculates the range using the base position's Z and the dz value.</li>
 *   <li>Returns a predicate that checks if an entity's Z coordinate is within the specified range.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for invalid arguments.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link ScopeArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[dz=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[dz=5] selects entities within 5 blocks along the Z-axis from the base position
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
public class DZ extends ScopeArgument {
    @Override
    public @Nullable Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        var z = basePos.getZ();
        var dz = Double.parseDouble(arguments[0]);
        return entity -> ParseUtils.checkBetween(z, z + dz, entity.getZ());
    }

    /**
     * Returns the key name for this selector argument ('dz').
     *
     * @return the string "dz"
     */
    @Override
    public String getKeyName() {
        return "dz";
    }
}
