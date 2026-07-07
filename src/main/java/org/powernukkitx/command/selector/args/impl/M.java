package org.powernukkitx.command.selector.args.impl;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.exceptions.SelectorSyntaxException;
import org.powernukkitx.command.selector.ParseUtils;
import org.powernukkitx.command.selector.SelectorType;
import org.powernukkitx.command.selector.args.CachedSimpleSelectorArgument;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Location;

import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'm' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'm' argument is used to filter players by their game mode, selecting only those whose game mode matches
 * (or does not match, if negated) the specified value. This is commonly used in selectors such as @a[m=creative] to select
 * all players in creative mode, or @a[m=!survival] to select all players not in survival mode.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'm' selector argument for game mode-based filtering.</li>
 *   <li>Allows negation by prefixing the argument with '!'.</li>
 *   <li>Parses the argument as a game mode string or number (e.g., 's', 'survival', '0', 'c', 'creative', '1', etc.).</li>
 *   <li>Returns a predicate that checks if a player's game mode matches (or does not match) the specified value.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @a[m=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @a[m=creative] selects all players in creative mode
 * // @a[m=!survival] selects all players not in survival mode
 * // @a[m=1] selects all players in creative mode
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Only one argument is allowed (throws if more).</li>
 *   <li>Argument can be negated by prefixing with '!'.</li>
 *   <li>Argument must be a valid game mode string or number.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CachedSimpleSelectorArgument
 * @see ParseUtils
 * @see SelectorType
 * @see CommandSender
 * @see Location
 * @see Entity
 * @see Player
 * @since PowerNukkitX 2.0.0
 */
public class M extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        var gmStr = arguments[0];
        boolean reversed = ParseUtils.checkReversed(gmStr);
        if (reversed) gmStr = gmStr.substring(1);
        final var gm = ParseUtils.parseGameMode(gmStr);
        return entity -> entity instanceof Player player && (reversed != (player.getGamemode() == gm));
    }

    @Override
    public String getKeyName() {
        return "m";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
