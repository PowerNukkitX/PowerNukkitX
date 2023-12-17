package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedFilterSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;


public class C extends CachedFilterSelectorArgument {
    @Override
    public Function<List<Entity>, List<Entity>> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var c = Integer.parseInt(arguments[0]);
        if (c == 0) throw new SelectorSyntaxException("C cannot be zero!");
        return entities -> {
            entities.sort(Comparator.comparingDouble(e -> e.distanceSquared(basePos)));
            if (c < 0)
                Collections.reverse(entities);
            return entities.subList(0, Math.abs(c));
        };
    }

    @Override
    public String getKeyName() {
        return "c";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
