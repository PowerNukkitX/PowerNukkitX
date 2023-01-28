package cn.nukkit.command.selector.args.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.selector.args.ISelectorArgument;

/**
 * args like x,y,z.
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public abstract class CoordinateArgument implements ISelectorArgument {

    @Override
    public int getPriority() {
        return 1;
    }
}
