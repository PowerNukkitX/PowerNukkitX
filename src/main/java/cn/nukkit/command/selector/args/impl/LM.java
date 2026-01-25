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
 * Selector argument implementation for the 'lm' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'lm' argument is used to filter players by their minimum experience level, selecting only those whose level is greater than or equal
 * to the specified value. This is commonly used in selectors such as @p[lm=10] to select players with level 10 or above.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'lm' selector argument for minimum experience level filtering.</li>
 *   <li>Validates that only one argument is provided and that it is not negated.</li>
 *   <li>Parses the argument as an integer and applies the filter to player entities.</li>
 *   <li>Returns a predicate that checks if a player's experience level is greater than or equal to the specified value.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @p[lm=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @p[lm=5] selects players with experience level 5 or above
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
public class LM extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var lm = Integer.parseInt(arguments[0]);
        return entity -> entity instanceof Player player && player.getExperienceLevel() >= lm;
    }

    @Override
    public String getKeyName() {
        return "lm";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
