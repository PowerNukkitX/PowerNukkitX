package cn.nukkit.command.selector.args.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.ISelectorArgument;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * args like dx,dy,dz.
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public abstract class ScopeArgument implements ISelectorArgument {
    @Override
    public int getPriority() {
        return 2;
    }

    @Nullable
    @Override
    public String getDefaultValue(Map<String, List<String>> values, SelectorType selectorType, CommandSender sender) {
        if (values.containsKey("dx") || values.containsKey("dy") || values.containsKey("dz"))
            return "0";
        return null;
    }
}
