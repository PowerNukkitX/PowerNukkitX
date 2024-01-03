package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.ISelectorArgument;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * args like dx,dy,dz.
 */


public abstract class ScopeArgument implements ISelectorArgument {
    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public @Nullable String getDefaultValue(Map<String, List<String>> values, SelectorType selectorType, CommandSender sender) {
        if (values.containsKey("dx") || values.containsKey("dy") || values.containsKey("dz"))
            return "0";
        return null;
    }
}
