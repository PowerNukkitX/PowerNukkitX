package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamNodeType;

public interface IParamNode<T> {
    void fill(String arg, Object... extras) throws CommandSyntaxException;

    <E> E get();

    void reset();

    boolean hasResult();

    boolean isOptional();

    ParamNodeType type();
}
