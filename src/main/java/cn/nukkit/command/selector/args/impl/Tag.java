package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.ArrayList;
import java.util.function.Predicate;


public class Tag extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var $1 = new ArrayList<String>();
        final var $2 = new ArrayList<String>();
        for (var tag : arguments) {
            boolean $3 = ParseUtils.checkReversed(tag);
            if (reversed) {
                tag = tag.substring(1);
                dontHave.add(tag);
            } else have.add(tag);
        }
        return entity -> have.stream().allMatch(entity::containTag) && dontHave.stream().noneMatch(entity::containTag);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getKeyName() {
        return "tag";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPriority() {
        return 4;
    }
}
