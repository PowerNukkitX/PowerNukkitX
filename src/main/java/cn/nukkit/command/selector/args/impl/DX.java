package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public class DX extends ScopeArgument {
    @Override
    public @Nullable Predicate<Entity> getPredicate(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        var x = basePos.getX();
        var dx = Double.parseDouble(arguments[0]);
        return entity -> ParseUtils.checkBetween(x, x + dx, entity.getX());
    }

    @Override
    public String getKeyName() {
        return "dx";
    }
}
