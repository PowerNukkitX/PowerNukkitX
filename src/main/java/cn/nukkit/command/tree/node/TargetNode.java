package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.tree.ParamNodeType;

import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public abstract class TargetNode<T> extends ParamNode<List<T>> {
    @Override
    public ParamNodeType type() {
        return ParamNodeType.TARGET;
    }
}
