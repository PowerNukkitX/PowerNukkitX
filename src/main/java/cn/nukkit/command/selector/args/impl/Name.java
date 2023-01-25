package cn.nukkit.command.selector.args.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.function.Predicate;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class Name extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        ParseUtils.singleArgument(arguments, getKeyName());
        var name = arguments[0];
        boolean reversed = ParseUtils.checkReversed(name);
        if (reversed) name = name.substring(1);
        String finalName = name;
        return entity -> reversed != entity.getName().equals(finalName);
    }

    @Override
    public String getKeyName() {
        return "name";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
