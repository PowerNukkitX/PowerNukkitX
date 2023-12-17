package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.SelectorSyntaxException;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.function.Predicate;


public class RYM extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) throws SelectorSyntaxException {
        ParseUtils.singleArgument(arguments, getKeyName());
        ParseUtils.cannotReversed(arguments[0]);
        final var rym = Double.parseDouble(arguments[0]);
        if (!ParseUtils.checkBetween(-180d, 180d, rym))
            throw new SelectorSyntaxException("RX out of bound (-180 - 180): " + rym);
        return entity -> ((entity.getYaw() + 90) % 360 - 180) >= rym;
    }

    @Override
    public String getKeyName() {
        return "rym";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
