package cn.nukkit.command.tree;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.tree.node.IParamNode;

import java.util.ArrayList;
import java.util.function.Predicate;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class ParamList extends ArrayList<IParamNode<?>> {
    private int error = -1;
    private int index = 0;
    public final ParamTree parent;

    public ParamList(ParamTree parent) {
        this.parent = parent;
    }

    public void reset() {
        this.error = -1;
        this.index = 0;
        forEach(IParamNode::reset);
    }

    public int getIndexAndIncrement() {
        return index++;
    }

    public void error() {
        this.error = index - 1;
    }

    public boolean isComplete() {
        return this.stream().filter(Predicate.not(IParamNode::isOptional)).allMatch(IParamNode::hasResult);
    }

    /**
     * 获取当前的参数链解析在哪个下标发生了错误(下标从0开始)
     *
     * @return the error index
     */
    public int getError() {
        return error;
    }

    /**
     * 获取当前的参数链解析了几个参数(下标从1开始)
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    public <E> E getResult(int index) {
        return this.get(index).get();
    }

    public boolean hasResult(int index) {
        return index < this.size() && index > -1 && this.get(index).get() != null;
    }

    @Override
    public ParamList clone() {
        ParamList v = (ParamList) super.clone();
        v.error = this.error;
        v.index = this.index;
        return v;
    }
}
