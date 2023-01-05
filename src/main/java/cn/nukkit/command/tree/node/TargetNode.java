package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public abstract class TargetNode<T> extends ParamNode<List<T>> {
    @Override
    public void error() {
        var list = this.getParent();
        list.error();
        list.setErrorMessage("commands.generic.noTargetMatch");
    }
}
