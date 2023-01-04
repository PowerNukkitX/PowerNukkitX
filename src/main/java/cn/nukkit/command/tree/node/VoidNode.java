package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.tree.ParamList;

/**
 * 一个用来占位的空参数节点
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class VoidNode implements IParamNode<Void> {
    @Override
    public void fill(String arg) {
    }

    @Override
    public <E> E get() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public ParamList getParent() {
        return null;
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public boolean isOptional() {
        return true;
    }
}
