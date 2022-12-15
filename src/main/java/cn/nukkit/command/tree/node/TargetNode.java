package cn.nukkit.command.tree.node;

import cn.nukkit.command.tree.ParamNodeType;

import java.util.List;

public abstract class TargetNode<T> extends ParamNode<List<T>> {
    @Override
    public ParamNodeType type() {
        return ParamNodeType.TARGET;
    }
}
