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
