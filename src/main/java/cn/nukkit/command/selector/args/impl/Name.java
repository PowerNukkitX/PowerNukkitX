package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'name' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'name' argument is used to filter entities by their name. It allows specifying required names (entities must match all specified names)
 * and excluded names (entities must not match any of the specified names). Negation is supported by prefixing a name with '!' (e.g., @e[name=!Steve]).
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'name' selector argument for entity name-based filtering.</li>
 *   <li>Allows multiple names, with negation for exclusion.</li>
 *   <li>Parses arguments into required and excluded name lists.</li>
 *   <li>Returns a predicate that checks if an entity matches all required names and none of the excluded names.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[name=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[name=Steve] selects entities named 'Steve'
 * // @e[name=!Alex] selects entities not named 'Alex'
 * // @e[name=Steve,Alex] selects entities named 'Steve' and 'Alex'
 * // @e[name=Steve,!Alex] selects entities named 'Steve' but not 'Alex'
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Multiple arguments are allowed (comma-separated).</li>
 *   <li>Negation is supported by prefixing with '!'.</li>
 *   <li>Empty or null arguments are ignored.</li>
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
public class Name extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var have = new ArrayList<String>();
        final var dontHave = new ArrayList<String>();
        for (var name : arguments) {
            boolean reversed = ParseUtils.checkReversed(name);
            if (reversed) {
                name = name.substring(1);
                dontHave.add(name);
            } else have.add(name);
        }
        return entity -> have.stream().allMatch(name -> entity.getName().equals(name)) && dontHave.stream().noneMatch(name -> entity.getName().equals(name));
    }

    @Override
    public String getKeyName() {
        return "name";
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
