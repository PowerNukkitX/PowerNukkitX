package cn.nukkit.command.selector.args.impl;

import cn.nukkit.command.selector.args.ISelectorArgument;

/**
 * args like x,y,z.
 */


public abstract class CoordinateArgument implements ISelectorArgument {

    @Override
    public int getPriority() {
        return 1;
    }
}
