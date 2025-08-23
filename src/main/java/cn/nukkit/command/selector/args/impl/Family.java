package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.ArrayList;
import java.util.function.Predicate;


public class Family extends CachedSimpleSelectorArgument {

    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var have     = new ArrayList<String>();
        final var dontHave = new ArrayList<String>();

        if (arguments != null) {
            for (var fam : arguments) {
                if (fam == null || fam.isEmpty()) continue;
                boolean reversed = ParseUtils.checkReversed(fam);
                if (reversed) {
                    fam = fam.substring(1);
                    if (!fam.isEmpty()) dontHave.add(fam);
                } else {
                    have.add(fam);
                }
            }
        }

        return entity ->
                (have.isEmpty() || entity.isAllFamilies(have))
                && (dontHave.isEmpty() || !entity.isAnyFamily(dontHave));
    }

    @Override
    public String getKeyName() {
        return "family";
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
