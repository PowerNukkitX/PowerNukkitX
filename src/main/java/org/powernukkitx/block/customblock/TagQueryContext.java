package org.powernukkitx.block.customblock;

import org.powernukkitx.molang.MolangContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Answers {@code query.any_tag} and {@code query.all_tags} from a block's or an item's tags, which is what
 * behaviour pack filters such as {@code block_filter}, {@code item_specific_speeds} and digger rules are
 * written against. Any other query reads as unknown.
 *
 * @param hasTag whether the block or item carries a tag
 */
public record TagQueryContext(@NotNull Predicate<String> hasTag) implements MolangContext {

    @Override
    public @Nullable Object query(@NotNull String name, @NotNull Object[] arguments) {
        return switch (name) {
            case "any_tag" -> {
                for (Object tag : arguments) {
                    if (hasTag.test(String.valueOf(tag))) {
                        yield true;
                    }
                }
                yield false;
            }
            case "all_tags" -> {
                for (Object tag : arguments) {
                    if (!hasTag.test(String.valueOf(tag))) {
                        yield false;
                    }
                }
                yield arguments.length > 0;
            }
            default -> null;
        };
    }
}
