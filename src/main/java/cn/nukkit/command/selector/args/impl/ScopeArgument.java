package cn.nukkit.command.selector.args.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.selector.args.ISelectorArgument;
import org.jetbrains.annotations.Nullable;

/**
 * args like dx,dy,dz.
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public abstract class ScopeArgument implements ISelectorArgument {

    @Override
    public int getPriority() {
        return 2;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return "0";
    }
}
