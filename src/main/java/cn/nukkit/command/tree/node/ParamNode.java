package cn.nukkit.command.tree.node;

public abstract class ParamNode<T> implements IParamNode<T> {
    protected T value = null;
    protected final boolean optional;

    public ParamNode(boolean optional) {
        this.optional = optional;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E get() {
        return (E) value;
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
}
