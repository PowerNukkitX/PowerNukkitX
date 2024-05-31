package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.ArrayList;
import java.util.function.Predicate;


public class Name extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var $1 = new ArrayList<String>();
        final var $2 = new ArrayList<String>();
        for (var name : arguments) {
            boolean $3 = ParseUtils.checkReversed(name);
            if (reversed) {
                name = name.substring(1);
                dontHave.add(name);
            } else have.add(name);
        }
        return entity -> have.stream().allMatch(name -> entity.getName().equals(name)) && dontHave.stream().noneMatch(name -> entity.getName().equals(name));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getKeyName() {
        return "name";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPriority() {
        return 4;
    }
}
