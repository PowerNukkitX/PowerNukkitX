package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.tree.ParamTree;

public abstract class ParamNode<T> implements IParamNode<T> {
    protected T value = null;
    protected boolean optional;
    protected ParamTree parent;

    @Override
    @SuppressWarnings("unchecked")
    public <E> E get() {
        if (this.isOptional()) {
            if (value == null) return null;
            else return (E) value;
        } else return (E) value;
    }

    @Override
    public boolean hasResult() {
        return value != null;
    }

    @Override
    public void reset() {
        this.value = null;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public IParamNode<T> init(ParamTree parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.parent = parent;
        this.optional = optional;
        return this;
    }
}
