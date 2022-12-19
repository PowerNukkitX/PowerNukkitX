package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;

public class VoidNode implements IParamNode<Void> {
    @Override
    public void fill(String arg) throws CommandSyntaxException {
    }

    @Override
    public <E> E get() {
        return null;
    }

    @Override
    public void reset() {
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
