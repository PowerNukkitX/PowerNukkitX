package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.ISelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public class RM implements ISelectorArgument {
    @Override
    public @Nullable Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var rm = Double.parseDouble(arguments[0]);
        return entity -> entity.distanceSquared(basePos) > Math.pow(rm, 2);
    }

    @Override
    public String getKeyName() {
        return "rm";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
