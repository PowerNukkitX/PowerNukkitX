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
 * Selector argument implementation for the 'tag' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'tag' argument is used to filter entities by their tags. It allows specifying required tags (entities must have all specified tags)
 * and excluded tags (entities must not have any of the specified tags). Negation is supported by prefixing a tag with '!' (e.g., @e[tag=!foo]).
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'tag' selector argument for entity tag-based filtering.</li>
 *   <li>Allows multiple tags, with negation for exclusion.</li>
 *   <li>Parses arguments into required and excluded tag lists.</li>
 *   <li>Returns a predicate that checks if an entity matches all required tags and none of the excluded tags.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[tag=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[tag=foo] selects entities with the 'foo' tag
 * // @e[tag=!bar] selects entities without the 'bar' tag
 * // @e[tag=foo,bar] selects entities with both 'foo' and 'bar' tags
 * // @e[tag=foo,!bar] selects entities with 'foo' but not 'bar'
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
public class Tag extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var have = new ArrayList<String>();
        final var dontHave = new ArrayList<String>();
        for (var tag : arguments) {
            boolean reversed = ParseUtils.checkReversed(tag);
            if (reversed) {
                tag = tag.substring(1);
                dontHave.add(tag);
            } else have.add(tag);
        }
        return entity -> have.stream().allMatch(entity::hasTag) && dontHave.stream().noneMatch(entity::hasTag);
    }

    @Override
    public String getKeyName() {
        return "tag";
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
