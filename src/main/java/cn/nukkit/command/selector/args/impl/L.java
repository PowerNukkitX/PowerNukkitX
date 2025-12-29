package cn.nukkit.command.selector.args.impl;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'l' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'l' argument is used to filter players by their experience level, selecting only those whose level is less than or equal
 * to the specified value. This is commonly used in selectors such as @p[l=10] to select players with level 10 or below.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'l' selector argument for maximum experience level filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Parses the argument as an integer and applies the filter to player entities.</li>
 *   <li>Returns a predicate that checks if a player's experience level is less than or equal to the specified value.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @p[l=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @p[l=5] selects players with experience level 5 or below
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument must not be negated (throws if starts with '!').</li>
 *   <li>Argument must be a valid integer.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CachedSimpleSelectorArgument
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @see cn.nukkit.Player
 * @since PowerNukkitX 2.0.0
 */
public class L extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var l = Integer.parseInt(arguments[0]);
        return entity -> entity instanceof Player player && player.getExperienceLevel() <= l;
    }

    /**
     * Returns the key name for this selector argument ('l').
     *
     * @return the string "l"
     */
    @Override
    public String getKeyName() {
        return "l";
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
